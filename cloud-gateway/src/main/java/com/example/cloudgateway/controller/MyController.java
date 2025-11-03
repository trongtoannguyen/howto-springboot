package com.example.cloudgateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.Reader;

@RestController
public class MyController {

    @RequestMapping("/fallback")
    public Mono<String> fallback(Reader reader) {
        return Mono.just("fallback");
    }
}
