package com.jinshuai.http;

import lombok.Builder;
import lombok.Data;

/**
 * @author: JS
 * @date: 2019/4/30
 * @description:
 */
@Data
@Builder
public class HttpResponse {

    private int responseCode;

    private static final String[][] RESPONSE_STATUS = {
            {"Continue", "Switching Protocols", "Processing", "Early Hints"},
            {"OK", "Created", "Accepted", "Non-authoritative Information", "No Content", "Reset Content", "Partial Content", "Multi-Status", "Already Reported"},
            {"Multiple Choices", "Moved Permanently", "Found", "See Other", "Not Modified", "Use Proxy", "Switch Proxy", "Temporary Redirect", "Permanent Redirect"},
            {"Bad Request", "Unauthorized", "Payment Required", "Forbidden", "Not Found", "Method Not Allowed", "Not Acceptable", "Proxy Authentication Required", "Request Timeout", "Conflict", "Gone", "Length Required", "Precondition Failed", "Payload Too Large", "Request-URI Too Long", "Unsupported Media Type", "Requested Range Not Satisfiable", "Expectation Failed", "I'm a teapot", null, null, "Misdirected Request", "Unprocessable Entity", "Locked", "Failed Dependency", null, "Upgrade Required", null, "Precondition Required", "Too Many Requests", null, "Request Header Fields Too Large", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "Unavailable For Legal Reasons"},
            {"Internal Server Error", "Not Implemented", "Bad Gateway", "Service Unavailable", "Gateway Timeout", "HTTP Version Not Supported", "Variant Also Negotiates", "Insufficient Storage", "Loop Detected", null, "Not Extended", "Network Authentication Required"}
    };

    private String contentType;

    private byte[] body;

    public String getResponseStatus(int responseCode) {
        return RESPONSE_STATUS[responseCode / 100 - 1][responseCode % 100];
    }

}