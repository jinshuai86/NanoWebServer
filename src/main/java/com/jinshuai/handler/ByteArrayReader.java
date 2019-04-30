package com.jinshuai.handler;

import lombok.Data;

/**
 * @author: JS
 * @date: 2019/4/30
 * @description:
 */
@Data
public class ByteArrayReader {

    private byte[] buf;

    public ByteArrayReader(byte[] buf) {
        this.buf = buf;
    }
    private int lineStart = 0;
    private int position = 0;

    public String nextLine() {
        String res = "";
        while (position < buf.length - 1) {
            if (buf[position] == '\r' && buf[position + 1] == '\n') {
                res = new String(buf, lineStart, position - lineStart);
                position += 2;
                lineStart = position;
                break;
            }
            position++;
        }
        return res;
    }

    public static void main(String[] args) {
        ByteArrayReader byteArrayReader = new ByteArrayReader("GET / HTTP/1.1\r\nHost: localhost:12345\r\n".getBytes());
        System.out.println(byteArrayReader.nextLine());
        System.out.println(byteArrayReader.nextLine());

        ByteArrayReader byteArrayReader2 = new ByteArrayReader("\r\n".getBytes());
        System.out.println(byteArrayReader2.nextLine().isEmpty());
    }

}
