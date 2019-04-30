package com.jinshuai.handler;

import com.jinshuai.http.HttpHandler;
import com.jinshuai.http.HttpRequest;
import com.jinshuai.http.HttpResponse;

import java.nio.charset.StandardCharsets;

/**
 * @author: JS
 * @date: 2019/4/30
 * @description:
 */
public class EchoHandler implements HttpHandler {

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        String body = "<!DOCTYPE html>\n" +
        "<html>\n" +
        "<head>\n" +
        "    <title>Welcome to JS WebServer!</title>\n" +
        "    <style>\n" +
        "        body {\n" +
        "            width: 35em;\n" +
        "            margin: 0 auto;\n" +
        "            font-family: Tahoma, Verdana, Arial, sans-serif;\n" +
        "        }\n"+ 
        "    </style>\n" + 
        "</head>\n" +
        "<body>\n" +
        "<h1>Welcome to JS WebServer!</h1>\n" +
        "<p>If you see this page, the JS WebServer is successfully installed and\n" +
        "    working. </p>\n" +
        "\n" +
        "<p>For online documentation and support please refer to\n" +
        "    <a href=\"https://github.com/jinshuai86/NanoWebServer\">JS WebServer</a>.<br/>\n" +
        "\n" +
        "<p><em>Thank you for using JS WebServer.</em></p>\n" +
        "</body>\n" +
        "</html>"; 
        HttpResponse response = HttpResponse.builder().responseCode(200).body(body.getBytes(StandardCharsets.UTF_8)).build();
        return response;
    }

}