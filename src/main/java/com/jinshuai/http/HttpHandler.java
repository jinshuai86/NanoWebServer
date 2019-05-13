package com.jinshuai.http;

/**
 * @author: JS
 * @date: 2019/4/30
 * @description:
 */
public interface HttpHandler {

    /**
     * 处理封装好的请求，返回封装好的响应
     * */
    HttpResponse handle(HttpRequest httpRequest);

}