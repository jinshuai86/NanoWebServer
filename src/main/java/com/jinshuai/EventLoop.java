package com.jinshuai;

import lombok.extern.slf4j.Slf4j;
import sun.nio.ch.SelectorImpl;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.Set;

/**
 * @author: JS
 * @date: 2019/4/29
 * @description:
 */
@Slf4j
public abstract class EventLoop extends Thread {

    private final Selector selector = Selector.open();

    private volatile boolean running = true;

    EventLoop(String name) throws IOException {
        super(name);
    }

    @Override
    public void run() {
        while (running) {
            try {
                int count = selector.select(100);
                if (count > 0) {
                    Set<SelectionKey> keySets = selector.selectedKeys();
                    keySets.forEach(key -> {
                        if (key.isValid()) {
                            process(key);
                        }
                        keySets.remove(key);
                    });
                }
            } catch (IOException e) {
                log.error("select() error", e);
            }
        }
    }

    void register(AbstractSelectableChannel channel, int op) {
        try {
            // SubReactor调用select已经阻塞，需要唤醒，否则无法注册。
            selector.wakeup();
            /*
                1. 当向同一个SubReactor注册时，需要获取到Select监控的Channel，并且需要提前获取到锁，锁是SelectionKeys集合。
                2. 由于SubReactor调用select已经阻塞，并且已经提前获取了SelectionKeys集合对应的锁，导致MainReactor永久阻塞，注册不上。
                3. 所以需要设置EventLoop的select(100)的超时时间。
             */
            channel.register(selector, op);
        } catch (ClosedChannelException e) {
            log.error("attempt to register a closed channel", e);
        }
    }

    abstract void process(SelectionKey key);

}