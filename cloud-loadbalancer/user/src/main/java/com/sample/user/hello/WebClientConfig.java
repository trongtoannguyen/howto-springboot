package com.sample.user.hello;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@LoadBalancerClient(name = "halo-world", configuration = HaloWorldConfiguration.class)
public class WebClientConfig {

    @Bean
    @LoadBalanced
    WebClient.Builder WebClientBuilder() {
        return WebClient.builder();
    }
}