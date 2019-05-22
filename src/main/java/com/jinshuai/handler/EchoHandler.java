package com.jinshuai.handler;

import com.jinshuai.common.PathContent;
import com.jinshuai.http.HttpHandler;
import com.jinshuai.http.HttpRequest;
import com.jinshuai.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author: JS
 * @date: 2019/4/30
 * @description:
 */
@Slf4j
public class EchoHandler implements HttpHandler {

    private String body;

    private int responseCode = 200;

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        String path = httpRequest.getPath().toLowerCase();
        path = "/".equals(path) ? INDEX_PATH : path;
        if (PathContent.pathContent.containsKey(path)) {
            body = PathContent.pathContent.get(path);
        } else {
            body = PathContent.pathContent.get(NOT_FOUND);
            responseCode = 404;
        }

        return HttpResponse
                .builder()
                .responseCode(responseCode)
                .body(body.getBytes(StandardCharsets.UTF_8))
                .build();
    }

    public static void main(String[] args) throws IOException {
        Path staticFilePath = Paths.get(EchoHandler.class.getResource("/").toString().substring(6) + "template/index.html");
        Files.readAllLines(staticFilePath).forEach(System.out::println);
    }

}