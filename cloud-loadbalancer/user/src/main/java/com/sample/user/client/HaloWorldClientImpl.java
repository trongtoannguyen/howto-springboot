package com.sample.user.client;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class HaloWorldClientImpl implements HaloWorldClient {
    private final WebClient webClient;

    public HaloWorldClientImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<String> fetchHome() {
        return webClient
                .get()
                .retrieve().bodyToMono(String.class);
    }

    @Override
    public Mono<String> getGreeting() {
        return webClient
                .get().uri("/greeting")
                .retrieve().bodyToMono(String.class);
    }
}
