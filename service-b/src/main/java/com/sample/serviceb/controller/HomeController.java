package com.sample.serviceb.controller;

import com.netflix.discovery.EurekaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);
    private final RestClient restClient;
    private final EurekaClient discoveryClient;

    public HomeController(EurekaClient discoveryClient, RestClient.Builder restClientBuilder) {
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
        var instanceInfo = discoveryClient.getNextServerFromEureka("service-a", false);
        var url = instanceInfo.getHomePageUrl() + "/health";
        return restClient.get().uri(url).retrieve().body(String.class);
    }
}
