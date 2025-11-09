package com.example.cloudgateway;

import com.example.cloudgateway.config.UriConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

import java.util.Locale;

@SpringBootApplication
@EnableConfigurationProperties(UriConfiguration.class)
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
    public RouteLocator myRoutes(RouteLocatorBuilder builder, UriConfiguration uriConfiguration) {
        String httpUri = uriConfiguration.getHttpbin(); // get uri from our configuration class
        return builder.routes()

                // $ curl --dump-header - http://localhost:8080/get
                .route(p -> p
                        .path("/get")
                        .filters(f -> f.addRequestHeader("Hello", "World"))
                        .uri(httpUri))

                // $ curl --dump-header - --header 'Host: www.circuitbreaker.com' http://localhost:8080/delay/3
                .route(p -> p
                        .host("*.circuitbreaker.com")
                        .filters(f -> f.circuitBreaker(config -> config
                                .setName("mycmd")
                                .setFallbackUri("forward:/fallback")))
                        .uri(httpUri))

                // $ curl --dump-header - --header 'Host: www.abc.org' http://localhost:8080/anything/png
                .route(p -> p
                        .host("*.abc.org").and()
                        .path("/anything/png")
                        .filters(f -> f
                                .addResponseHeader("X-FooHeader", "png"))
                        .uri(httpUri))

                // $ curl -X POST -D - --header 'Host: www.statuscode.org' --data 'code' http://localhost:8080/200
                .route("read_body_pred", p -> p
                        .method(HttpMethod.POST).and()
                        .host("*.statuscode.org").and()
                        // any integer pattern
                        .path("/{code:\\d+}").and()
                        .readBody(String.class, s -> s.trim()
                                .equalsIgnoreCase("code"))
                        .filters(f -> f
                                .prefixPath("/status")
                                .addResponseHeader("X-TestHeader", "read_body_pred"))
                        .uri(httpUri))

                // curl --dump-header - --header 'Host: www.rewriteresponseobj.org' --data 'hello' http://localhost:8080/
                .route("rewrite_response_upper", r -> r
                        .host("*.rewriteresponseupper.org").and()
                        .path("/xml")
                        .filters(f -> f
                                .addResponseHeader("X-TestHeader", "rewrite_response_upper")
                                .modifyResponseBody(String.class, String.class,
                                        (serverWebExchange, s) -> Mono.just(s.toUpperCase(Locale.ROOT))))
                        .uri(httpUri))
                .build();
    }
    // end::route-locator[]

    public record Hello(String message) {
    }
}
