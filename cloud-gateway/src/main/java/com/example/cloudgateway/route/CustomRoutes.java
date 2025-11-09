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
        //@formatter:off
        String uri = uriConfiguration.getHttpbin();
        String ws = uriConfiguration.getWs();

        return builder.routes()
                .route("path_route", r -> r.order(9999).path("/get")
                        .filters(p -> p.addResponseHeader("Halo", "Foobar")
                                .addResponseHeader("X-Response-Default-Foo", "Default-Bar"))
                        .uri(uri))

                .route("host_route", r -> r.host("*.myhost.org")
                        .filters(p -> p.addResponseHeader("X-Response-Foo", "MyHost"))
                        .uri(uri))

                .route("rewrite_route", r -> r.host("*.rewrite.org")
                        .filters(f -> f.addResponseHeader("X-Rewrite-Response", "Rewritten")
                                .rewritePath("/foo/(?<segment>.*)", "/${segment}")) // /foo/cookies -> /cookies
                        .uri(uri))

                // $ curl -D - -H 'Host: www.circuitbreaker.org' http://localhost:8080/delay/3
                .route("circuitbreaker_route", r -> r.host("*.circuitbreaker.org")
                        .filters(f -> f.circuitBreaker(config -> config.setName("slowcmd"))
                                .addResponseHeader("X-CircuitBreaker", "OKSLOWCMD"))
                        .uri(uri))

                .route("circuitbreaker_fallback_route", r -> r.host("*.circuitbreakerfallback.org")
                        .filters(f -> f.circuitBreaker(config -> config.setName("slowcmd")
                                        .setFallbackUri("forward:/circuitbreaker-fallback"))
                                .addResponseHeader("X-CircuitBreaker", "NotTriggered"))
                        .uri(uri))

                .route("limit_route", r -> r.host("*.limited.org").and().path("/anything/**")
                        .filters(f -> f.requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter()))
                                .addResponseHeader("X-RateLimiter", "Applied"))
                        .uri(uri))

                .route("websocket_route", r -> r.path("/echo")
                        .filters(f -> f.addResponseHeader("X-WebSocket", "Enabled"))
                        .uri(ws))

                .build();
        //@formatter:on
    }

    /**
     * Rate limiter bean mục đích giới hạn lưu lượng thực hiện request tới services
     */
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(1, 2);
    }
}
