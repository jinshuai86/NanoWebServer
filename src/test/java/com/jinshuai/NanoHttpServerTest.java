package com.jinshuai;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * test server
 */
public class NanoHttpServerTest {

    @Test
    public void testClient() {
        try {
            int i = 1;
            while (i-- > 0) {
                Socket socket = new Socket("127.0.0.1", 12345);
                OutputStream outputStream = socket.getOutputStream();
                String input = "msg--" + i;
//                for (int i1 = 0; i1 < 1014; i1++) {
//                    input += i1;
//                }
                outputStream.write(input.getBytes());
//                outputStream.flush();
//                Thread.sleep(1000);
//                outputStream.write("later".getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                InputStream inputStreamFromServer = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStreamFromServer.read(buffer)) != -1) {
                    System.out.println(new String(buffer, 0, len));
                }
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testServer() throws IOException, InterruptedException {
        HttpServer server = new HttpServer(12345);
    }
}