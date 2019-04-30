package com.jinshuai;

import com.jinshuai.handler.EchoHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * @author: JS
 * @date: 2018/12/20
 * @description: server v1.0
 */
@Slf4j
public class HttpServer {

    private int port = 8080;

    private final int PROCESSORS = Runtime.getRuntime().availableProcessors();

    private SubReactor[] subReactors = new SubReactor[PROCESSORS * 2];

    private void start() throws IOException {
        // 启动MainReactor
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(port));
        MainReactor mainReactor = new MainReactor(serverSocketChannel, subReactors, new EchoHandler());
        mainReactor.start();
        // 启动SubReactor
        for (int i = 0; i < subReactors.length; i++) {
            subReactors[i] = new SubReactor(i);
            subReactors[i].start();
        }
        log.info("server start on port: {}", port);
    }

    private void stop() {

    }

    HttpServer(int port) {
        if (port < 1 || port > 65535) {
            log.error("port is illegal: {}", port);
            return;
        }
        this.port = port;
    }

    public static void main(String[] args) throws IOException {
        new HttpServer(12345).start();
    }

}