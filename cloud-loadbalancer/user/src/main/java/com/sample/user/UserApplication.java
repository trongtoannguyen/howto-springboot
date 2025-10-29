package com.sample.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@SpringBootApplication
public class UserApplication {

    private static final Logger log = LoggerFactory.getLogger(UserApplication.class);
    private final WebClient webClient;
    private final ReactorLoadBalancerExchangeFilterFunction lbFunction;

    public UserApplication(WebClient.Builder webClientBuilder, ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        this.webClient = webClientBuilder.build();
        this.lbFunction = lbFunction;
    }

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

    @GetMapping
    public Mono<String> home() {
        return webClient
                .get().uri("http://halo-world/")
                .retrieve().bodyToMono(String.class);
    }

    @GetMapping("/hi")
    public Mono<String> hi(@RequestParam(value = "name", defaultValue = "Fred") String name) {
        return webClient
                .get().uri("http://halo-world/greeting")
                .retrieve().bodyToMono(String.class)
                .map(greeting -> String.format("%s, %s!", greeting, name));
    }

    @GetMapping("/halo")
    public Mono<String> halo(@RequestParam(value = "name", defaultValue = "Powell") String name) {
        return WebClient.builder().filter(lbFunction).build()
                .get().uri("http://halo-world/greeting")
                .retrieve().bodyToMono(String.class)
                .map(gre -> String.format("%s, %s", gre, name));
    }
}
