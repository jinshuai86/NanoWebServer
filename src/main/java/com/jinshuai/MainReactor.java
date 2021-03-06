package com.jinshuai;

import com.jinshuai.http.HttpHandler;
import com.jinshuai.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: JS
 * @date: 2019/4/29
 * @description:
 */
@Slf4j
class MainReactor extends EventLoop {

    private AtomicInteger subReactorIndex = new AtomicInteger(0);

    private SubReactor[] subReactors;

    private HttpHandler httpHandler;

    MainReactor(ServerSocketChannel serverSocketChannel, SubReactor[] subReactors, HttpHandler httpHandler) throws IOException {
        super("MainReactor");
        this.subReactors = subReactors;
        this.httpHandler = httpHandler;
        register(serverSocketChannel, SelectionKey.OP_ACCEPT, null);
    }

    @Override
    void process(SelectionKey key) {
        if (key.isAcceptable()) {
            try {
                ServerSocketChannel acceptSocketChannel = (ServerSocketChannel) key.channel();
                SocketChannel socketChannel = acceptSocketChannel.accept();
                log.debug("a new connect from [{}]", socketChannel.getRemoteAddress());
                socketChannel.configureBlocking(false);
                // current session
                HttpSession session = new HttpSession(socketChannel, httpHandler);
                subReactors[subReactorIndex.getAndAdd(1) % subReactors.length].register(socketChannel, SelectionKey.OP_READ, session);
                sessions.offer(new WeakReference<>(session));
            } catch (IOException e) {
                log.error("process key failed", e);
            }
        }

    }

}