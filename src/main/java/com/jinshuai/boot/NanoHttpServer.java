package com.jinshuai.boot;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * @author: JS
 * @date: 2018/12/20
 * @description: server v1.0
 */
public class NanoHttpServer {

    private void simpleServer() throws IOException {
        // 创建多路复选器管理在它这里注册的socket(监听型、主动型)
        Selector selector = Selector.open();
        // 创建监听型socket
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 对socket的操作改为非阻塞(IO多路复用搭配非阻塞IO防止当某个socket可用了，内核校验该socket数据时由于校验错误丢弃该数据，此时线程读取socket会阻塞线程)
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(12345));
        // 注册到多路复选器，并且感兴趣的事件是：新连接到来
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        // select()导致当前线程阻塞 => 直到有可用的socket => 客户端轮询所有socket => 找出可用socket
        while (selector.select() > 0) {
            Set<SelectionKey> keys = selector.selectedKeys();
            keys.forEach(key -> {
                try {
                    /* 之前注册的监听型的socket(serverSocketChannel)可用了(有新请求了)
                       通过accept()创建针对此次请求的主动型socket
                       注册到selector
                    */
                    if (key.isAcceptable()) {
                        ServerSocketChannel acceptSocketChannel = (ServerSocketChannel) key.channel();
                        SocketChannel socketChannel = acceptSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector,SelectionKey.OP_READ);
                        System.out.println("accept request from " + socketChannel.getRemoteAddress());
                    } else if (key.isReadable()) {
                        // 获取key对应的channel
                        SocketChannel socketChannel = (SocketChannel)key.channel();
                        // 分配1024字节大小的缓冲区
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        // 将channel数据读到缓冲区中(区别内核缓冲区，是将内核缓冲区中的数据读到进程地址空间中)
                        int count = socketChannel.read(byteBuffer);
                        // 将Buffer从写模式切换到读模式(重置position)
                        byteBuffer.flip();
                        // 数据不可用
                        if (count < 0) {
                            key.cancel();
                            socketChannel.close();
                            System.out.println("received invalid message");
                        } else {
                            // 接收数据
                            String receiveMsg = new String(byteBuffer.array());
                            System.out.println("msg from client " + receiveMsg);
                            // 发送数据
                            ByteBuffer outBuffer = ByteBuffer.wrap(("msg from server " + receiveMsg).getBytes());
                            socketChannel.write(outBuffer);
                            // 结束此socket，否则客户端一直阻塞等待数据
                            socketChannel.close();
                            // 清除缓冲区
                            byteBuffer.clear();
                            outBuffer.clear();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(-1);
                } finally {
                    // 将set中关于此socket的引用移除，否则会重复处理
                    keys.remove(key);
                }
            });
        }
    }

    public static void main(String[] args) throws IOException {
        new NanoHttpServer().simpleServer();
    }

}
