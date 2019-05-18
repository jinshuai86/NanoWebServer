package com.jinshuai.handler;

import com.jinshuai.http.HttpHandler;
import com.jinshuai.http.HttpRequest;
import com.jinshuai.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
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

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        StringBuilder body = new StringBuilder();
        String path = httpRequest.getPath().toLowerCase();
        path = "/".equals(path) ? INDEX_PATH : path;

        Path staticFilePath = Paths.get(BASE_PATH + path);
        try {
            if (Files.exists(staticFilePath) && !Files.isDirectory(staticFilePath)) {
                Files.readAllLines(staticFilePath).forEach(body::append);
            } else {
                staticFilePath = Paths.get(BASE_PATH + NOT_FOUND);
                Files.readAllLines(staticFilePath).forEach(body::append);
            }
        } catch (IOException e) {
            log.error("读取文件[{}]失败", staticFilePath, e);
        }
        HttpResponse response = HttpResponse.builder().responseCode(200).body(body.toString().getBytes(StandardCharsets.UTF_8)).build();
        return response;
    }

    public static void main(String[] args) throws IOException {
        Path staticFilePath = Paths.get(EchoHandler.class.getResource("/").toString().substring(6) + "index.html");
        Files.readAllLines(staticFilePath).forEach(System.out::println);
    }

}