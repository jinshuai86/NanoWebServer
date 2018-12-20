package com.jinshuai;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * test server
 */
public class NanoHttpServerTest {

    @Test
    public void testClient() {
        System.out.println("startup");
        try {
            int i = 5;
            while (i-- > 0) {
                Socket socket = new Socket("127.0.0.1", 12345);
                OutputStream outputStream = socket.getOutputStream();
                String input = "msg--" + i;
                outputStream.write(input.getBytes());
                outputStream.flush();
                InputStream inputStreamFromServer = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStreamFromServer.read(buffer)) != -1) {
                    System.out.println(new String(buffer, 0, len));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}