package com.example.cloudgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CloudGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudGatewayApplication.class, args);
    }


    /**
     * Defines custom route configurations for the application.
     *
     * @param builder the RouteLocatorBuilder used to configure and build routes
     * @return a RouteLocator containing the defined route configurations
     */
    // tag::route-locator[]
    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/get")
                        .filters(f -> f.addRequestHeader("Hello", "World"))
                        .uri("http://httpbin.org:80"))

                // $ curl --dump-header - --header 'Host: www.circuitbreaker.com' http://localhost:8080/delay/3
                .route(p -> p
                        .host("*.circuitbreaker.com")
                        .filters(f -> f.circuitBreaker(config -> config
                                .setName("mycmd")
                                .setFallbackUri("forward:/fallback")))
                        .uri("http://httpbin.org:80"))
                .build();
    }
    // end::route-locator[]
}
