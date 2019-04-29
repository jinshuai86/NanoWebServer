package com.jinshuai;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author: JS
 * @date: 2019/4/29
 * @description:
 */
@Slf4j
public class SubReactor extends EventLoop {

    public ByteBuffer inBuffer = ByteBuffer.allocate(1024);

    SubReactor(int index) throws IOException {
        super("SubReactor-" + index);
    }

    @Override
    void process(SelectionKey key) {
        if (key.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            try {
                int count = socketChannel.read(inBuffer);
                if (count > 0) {
                    String msg = new String(inBuffer.array());
                    log.info("receive msg \" {} \" from client {}", msg, socketChannel.getRemoteAddress());
                    inBuffer.clear();
                    // response
                    ByteBuffer outBuffer = ByteBuffer.wrap(("server: hello ").getBytes(StandardCharsets.UTF_8));
                    socketChannel.write(outBuffer);
                }
            } catch (IOException e) {
                log.error("subReactor read buffer exception", e);
            } finally {
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    log.error("failed to close the channel", e);
                }
            }
        }

    }

}
