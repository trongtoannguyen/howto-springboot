package com.sample.user.client;

import reactor.core.publisher.Mono;

public interface HaloWorldClient {
    Mono<String> fetchHome();

    Mono<String> getGreeting();
}
