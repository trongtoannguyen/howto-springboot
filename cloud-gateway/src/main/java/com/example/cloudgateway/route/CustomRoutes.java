package com.example.cloudgateway.route;

import com.example.cloudgateway.config.UriConfiguration;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomRoutes {

    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder, UriConfiguration uriConfiguration) {

        String uri = uriConfiguration.getHttpbin();
        return builder.routes()
                .route("path_route", r -> r.order(9999).path("/get")
                        .filters(p -> p.addResponseHeader("X-Response-Default-Foo", "Default-Bar"))
                        .uri(uri))

                .route("host_route", r -> r.host("*.myhost.org")
                        .filters(p -> p.addResponseHeader("X-Response-Foo", "MyHost"))
                        .uri(uri))

                .route("rewrite_route", r -> r.host("*.rewrite.org")
                        .filters(f -> f.addResponseHeader("X-Rewrite-Response", "Rewritten")
                                .rewritePath("/foo/(?<segment>.*)", "/${segment}")) // /foo/cookies -> /cookies
                        .uri(uri))

                .route("circuitbreaker_route", r -> r.host("*.circuitbreaker.org")
                        .filters(f -> f.circuitBreaker(config -> config.setName("slowcmd"))
                                .addResponseHeader("X-CircuitBreaker", "SLOWCMD"))
                        .uri(uri))

                .route("circuitbreaker_fallback_route", r -> r.host("*.circuitbreakerfallback.org")
                        .filters(f -> f.circuitBreaker(config -> config.setName("slowcmd")
                                .setFallbackUri("forward:/fallback")
                        ).addResponseHeader("X-CircuitBreaker", "Fallback"))
                        .uri(uri))

                .route("limit_route", r -> r.host("*.limited.org").and().path("/anything/**")
                        .filters(f -> f.requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())))
                        .uri(uri))


                .build();
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(1, 2);
    }
}
