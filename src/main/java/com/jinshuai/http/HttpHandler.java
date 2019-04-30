package com.jinshuai.http;

import com.jinshuai.http.HttpRequest;
import com.jinshuai.http.HttpResponse;

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
