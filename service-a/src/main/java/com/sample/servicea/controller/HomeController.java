package com.sample.servicea.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "I am Service A, hello world!";
    }

    @GetMapping("/health")
    public String health() {
        return "I am Service A, healthy!";
    }
}
