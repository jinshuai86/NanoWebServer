package com.jinshuai.http;

import com.jinshuai.handler.ByteArrayReader;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author: JS
 * @date: 2019/4/30
 * @description: as a session for client and server
 */
@Slf4j
public class HttpSession {

    private static final Set<String> VALID_METHOD = new HashSet<>();

    private long lastActive = System.currentTimeMillis();

    static {
        Collections.addAll(VALID_METHOD, "GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "TRACE");
    }

    private String method, path, protocol;

    private Map<String, List<String>> headers = new HashMap<>();

    private byte[] buf = new byte[2048];

    private int contentLength = -1;

    private byte[] body;

    private State state = State.START;

    private enum State {
        START,
        HEADER,
        BODY,
    }

    private SocketChannel socketChannel;

    private HttpHandler httpHandler;

    public HttpSession(SocketChannel socketChannel, HttpHandler httpHandler) {
        this.socketChannel = socketChannel;
        this.httpHandler = httpHandler;
    }

    public void processBuffer(ByteBuffer inBuffer) throws IOException {
//        byte[] buf = inBuffer.array();
        inBuffer.get(buf, 0, inBuffer.limit());
        ByteArrayReader reader = new ByteArrayReader(buf);
        /*
        handle METHOD、PATH、PROTOCOL
        handle HEADERS
        handle BODY
        */
        String line;
        headers.clear();
        if (state == State.START || state == State.HEADER) {
            while ((line = reader.nextLine()) != null) {
                lastActive = System.currentTimeMillis();
                log.debug("{} update last active time",  socketChannel.getRemoteAddress());
                if (state == State.START) {
                    if (checkStart(line)) {
                        state = State.HEADER;
                    } else {
                        log.debug("start error");
                        error();
                        return;
                    }
                } else if (state == State.HEADER) {
                    // \r\n
                    if (line.isEmpty()) {
                        if (contentLength < 0) {
                            finish();
                            state = State.START;
                            return;
                        } else {
                            state = State.BODY;
                        }
                    } else {
                        String[] header = line.split(":");
                        if (header.length >= 2) {
                            String key = header[0].trim();
                            String value = header[1].trim().toLowerCase();
                            headers.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
                            if ("content-length".equalsIgnoreCase(key)) {
                                int contentLength = Integer.parseInt(value);
                                body = new byte[contentLength];
                            }
                        } else {
                            log.debug("header error");
                            error();
                            state = State.START;
                            return;
                        }
                    }
                }
            }
        }
        if (state == State.BODY) {
            int pos = reader.getPosition();
            System.arraycopy(buf, pos, body, 0, body.length);
            finish();
            state = State.START;
        }
    }

    private boolean checkStart(String line) {
        boolean res = true;
        // GET / HTTP/1.1
        String[] mpp = line.split(" ", 3);
        if (mpp.length < 3 || !VALID_METHOD.contains(mpp[0])) {
            res = false;
        } else {
            method = mpp[0];
            path = mpp[1];
            protocol = mpp[2];
        }
        return res;
    }

    private void error() throws IOException {
        HttpResponse response = HttpResponse.builder().responseCode(400).body("<h1>syntax error</h1>".getBytes(StandardCharsets.UTF_8)).build();
        writeResponse(response);
    }

    private void finish() throws IOException {
        HttpRequest request = HttpRequest.builder()
                .method(method)
                .path(path)
                .protocol(protocol)
                .headers(headers)
                .body(body)
                .remoteAddress(socketChannel.getRemoteAddress())
                .build();
        HttpResponse httpResponse = httpHandler.handle(request);
        writeResponse(httpResponse);
    }

    private void writeResponse(HttpResponse response) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 ").append(response.getResponseCode()).append(' ').append(response.getResponseStatus(response.getResponseCode())).append("\r\n");
        sb.append("Server: ").append("JSServer").append("\r\n");
        sb.append("Content-Type: ").append("html").append("\r\n");

        String welContent = new String(response.getBody(), StandardCharsets.UTF_8);
        byte[] welContentByte = welContent.getBytes(StandardCharsets.UTF_8);
        sb.append("Content-Length: ").append(welContentByte.length).append("\r\n");

        sb.append("\r\n").append(welContent);

        socketChannel.write(ByteBuffer.wrap(sb.toString().getBytes()));
    }

    public void checkIdle(int idleTime) {
        if (System.currentTimeMillis() - lastActive >= idleTime * 1000) {
            try {
                if (socketChannel.isOpen()) {
                    log.debug("socket timeout: {}, close the socket: {}", System.currentTimeMillis() - lastActive, socketChannel);
                    socketChannel.close();
                }
            } catch (IOException e) {
                log.error("close the channel error", e);
            }
        }
    }

}
