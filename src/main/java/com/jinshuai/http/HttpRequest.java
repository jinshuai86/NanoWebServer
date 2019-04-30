package com.jinshuai.http;

import lombok.Builder;
import lombok.Data;

import java.net.SocketAddress;
import java.util.List;
import java.util.Map;

/**
 * @author: JS
 * @date: 2019/4/30
 * @description:
 */
@Data
@Builder
public class HttpRequest {

    private String method;

    private String path;

    private String protocol;

    private Map<String, List<String>> headers;

    private byte[] body;

    private SocketAddress remoteAddress;

}