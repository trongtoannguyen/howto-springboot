package com.example.cloudgateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "uri")
public class UriConfiguration {

    @Value("${httpbin:http://httpbin.org:80}")
    private String httpbin;

    @Value("${ws:ws://echo.websocket.org}")
    private String ws;

    public String getHttpbin() {
        return httpbin;
    }

    public void setHttpbin(String httpbin) {
        this.httpbin = httpbin;
    }

    public String getWs() {
        return ws;
    }

    public void setWs(String ws) {
        this.ws = ws;
    }
}