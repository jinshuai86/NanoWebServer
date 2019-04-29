package com.jinshuai;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author: JS
 * @date: 2019/4/29
 * @description:
 */
@Slf4j
class MainReactor extends EventLoop {

    private int subReactorIndex = 0;

    private SubReactor[] subReactors;

    MainReactor(ServerSocketChannel serverSocketChannel, SubReactor[] subReactors) throws IOException {
        super("MainReactor");
        this.subReactors = subReactors;
        register(serverSocketChannel, SelectionKey.OP_ACCEPT);
    }

    @Override
    void process(SelectionKey key) {
        if (key.isAcceptable()) {
            try {
                ServerSocketChannel acceptSocketChannel = (ServerSocketChannel) key.channel();
                SocketChannel socketChannel = acceptSocketChannel.accept();
                socketChannel.configureBlocking(false);
//                subReactors[0].register(socketChannel, SelectionKey.OP_READ);
                subReactors[subReactorIndex++ % subReactors.length].register(socketChannel, SelectionKey.OP_READ);
                log.info("a new connect from [{}]", socketChannel.getRemoteAddress());
            } catch (IOException e) {
                log.error("process key failed", e);
            }
        }

    }

}