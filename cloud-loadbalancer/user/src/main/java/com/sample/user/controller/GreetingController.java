package com.sample.user.controller;

import com.sample.user.service.GreetingService;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class GreetingController {

    private final LoadBalancedExchangeFilterFunction lbFunction;
    private final GreetingService greetingService;

    public GreetingController(LoadBalancedExchangeFilterFunction lbFunction, GreetingService greetingService) {
        this.lbFunction = lbFunction;
        this.greetingService = greetingService;
    }

    @GetMapping
    public Mono<String> home() {
        return greetingService.greet();
    }

    @GetMapping("/hi")
    public Mono<String> hi(@RequestParam(value = "name", defaultValue = "Fred") String name) {
        return greetingService.greet(name);
    }

    // another mechanism to call halo-world
    @GetMapping("/halo")
    public Mono<String> halo(@RequestParam(value = "name", defaultValue = "Powell") String name) {
        return WebClient.builder().filter(lbFunction).build()
                .get().uri("http://halo-world/greeting")
                .retrieve().bodyToMono(String.class)
                .map(gre -> String.format("%s, %s", gre, name));
    }
}
