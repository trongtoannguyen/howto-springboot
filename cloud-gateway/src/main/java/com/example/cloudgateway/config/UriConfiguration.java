package com.example.cloudgateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "uri")
public class UriConfiguration {

    @Value("${uri.httpbin:http://localhost:80}")
    private String httpbin;

    public String getHttpbin() {
        return httpbin;
    }

    public void setHttpbin(String httpbin) {
        this.httpbin = httpbin;
    }
}