package com.sample.serviceb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
public class Home {

    private static final Logger log = LoggerFactory.getLogger(Home.class);
    private final EurekaDiscoveryClient discoveryClient;
    private final RestClient restClient;

    public Home(EurekaDiscoveryClient discoveryClient, RestClient.Builder restClientBuilder) {
        this.discoveryClient = discoveryClient;
        restClient = restClientBuilder.build();
    }

    @GetMapping("/")
    public String index() {
        return "I am service B. <br/>" +
                "Click <a href='/hello-eureka-service-a'>here</a> to call service A.";
    }

    @GetMapping("/hello-eureka-service-a")
    public String hello() {
        var listInstances = discoveryClient.getInstances("service-a");
        log.info("List instances: {}", listInstances);
        ServiceInstance serviceInstance = discoveryClient.getInstances("service-a").getFirst();
        return restClient.get()
                .uri(serviceInstance.getUri())
                .retrieve().body(String.class);
    }
}
