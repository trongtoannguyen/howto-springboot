package com.sample.user.service;

import reactor.core.publisher.Mono;

public interface GreetingService {
    Mono<String> greet();

    Mono<String> greet(String name);
}
