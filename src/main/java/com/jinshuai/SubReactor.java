package com.jinshuai;

import com.jinshuai.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author: JS
 * @date: 2019/4/29
 * @description:
 */
@Slf4j
class SubReactor extends EventLoop {

    private ByteBuffer inBuffer = ByteBuffer.allocateDirect(1024);

    SubReactor(int index) throws IOException {
        super("SubReactor-" + index);
    }

    @Override
    void process(SelectionKey key) {
        if (key.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            try {
                log.debug("receive msg from client {}", socketChannel.getRemoteAddress());
                HttpSession serverSession = (HttpSession) key.attachment();
                int count = socketChannel.read(inBuffer);
                if (count < 0) {
                    log.debug("close the channel: {}, localaddress: {}, remoteaddress: {}", socketChannel, socketChannel.getLocalAddress(), socketChannel.getRemoteAddress());
                    socketChannel.close();
                } else if (count > 0) {
                    inBuffer.flip();
                    serverSession.processBuffer(inBuffer);
                    inBuffer.clear();
                }
            } catch (IOException e) {
                try {
                    socketChannel.close();
                } catch (IOException e1) {
                    log.error("failed to close the channel", e1);
                }
            }
        }

    }

}