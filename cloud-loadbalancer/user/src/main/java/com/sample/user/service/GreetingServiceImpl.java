package com.sample.user.service;

import com.sample.user.client.HaloWorldClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GreetingServiceImpl implements GreetingService {
    private final HaloWorldClient haloWorldClient;

    public GreetingServiceImpl(HaloWorldClient haloWorldClient) {
        this.haloWorldClient = haloWorldClient;
    }

    @Override
    public Mono<String> greet() {
        return haloWorldClient.fetchHome();
    }

    @Override
    public Mono<String> greet(String name) {
        return haloWorldClient.getGreeting()
                .map(greeting -> String.format("%s, %s!", greeting, name));
    }
}
