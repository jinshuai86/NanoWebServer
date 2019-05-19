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

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        String path = httpRequest.getPath().toLowerCase();
        path = "/".equals(path) ? INDEX_PATH : path;

        body = PathContent.pathContent.containsKey(path) ? PathContent.pathContent.get(path) : PathContent.pathContent.get(NOT_FOUND);

        return HttpResponse
                .builder()
                .responseCode(200)
                .body(body.getBytes(StandardCharsets.UTF_8))
                .build();
    }

    public static void main(String[] args) throws IOException {
        Path staticFilePath = Paths.get(EchoHandler.class.getResource("/").toString().substring(6) + "template/index.html");
        Files.readAllLines(staticFilePath).forEach(System.out::println);
    }

}