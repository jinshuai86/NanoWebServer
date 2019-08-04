package com.jinshuai;

import com.jinshuai.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: JS
 * @date: 2019/4/29
 * @description:
 */
@Slf4j
public abstract class EventLoop extends Thread {

    public static ConcurrentLinkedQueue<WeakReference<HttpSession>> sessions = new ConcurrentLinkedQueue<>();

    private final Selector selector = Selector.open();

    private final Lock regLock = new ReentrantLock();

    EventLoop(String name) throws IOException {
        super(name);
    }

    @Override
    public void run() {
        while (HttpServer.running) {
            try {
                int count = selector.select();
                if (count > 0) {
                    Set<SelectionKey> keySets = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = keySets.iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        keyIterator.remove();
                        if (key.isValid()) {
                            process(key);
                        }
                    }
                }
            } catch (IOException e) {
                log.error("select() error", e);
            }
            regLock.lock();
            regLock.unlock();
        }
    }

    /**
     * @param attachment session for this request
     *
     * */
    void register(AbstractSelectableChannel channel, int op, Object attachment) {
        try {
            regLock.lock();
            selector.wakeup();
            channel.register(selector, op, attachment);
        } catch (ClosedChannelException e) {
            log.error("attempt to register a closed channel", e);
        } finally {
            regLock.unlock();
        }
    }

    abstract void process(SelectionKey key);

}