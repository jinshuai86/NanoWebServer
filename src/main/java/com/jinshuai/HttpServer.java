package com.jinshuai;

import com.jinshuai.common.PathContent;
import com.jinshuai.handler.EchoHandler;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

/**
 * @author: JS
 * @date: 2018/12/20
 * @description: server v1.0
 */
@Slf4j
public class HttpServer {

    public static volatile boolean running = false;

    private int port;

    private final int PROCESSORS = Runtime.getRuntime().availableProcessors();

    private SubReactor[] subReactors = new SubReactor[PROCESSORS * 2];

    private void start() {
        running = true;
        try {
            fillContent();
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
            log.debug("JSServer start on port: {}", port);
        } catch (IOException ioe) {
            log.error("failed start ", ioe);
        }
    }

    private void fillContent() throws IOException {
        String[] paths = {"/index.html","/404.html"};
        for (String path : paths) {
            InputStream inputStream = this.getClass().getResourceAsStream(path);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String content;
            while ((content = bufferedReader.readLine()) != null) {
                sb.append(content);
            }
            PathContent.pathContent.put(path, sb.toString());
        }

    }

    HttpServer(int port) {
        this.port = port;
    }

    private void stop() {
        running = false;
    }

    public static void main(String[] args) {
        new HttpServer(12345).start();
    }

}