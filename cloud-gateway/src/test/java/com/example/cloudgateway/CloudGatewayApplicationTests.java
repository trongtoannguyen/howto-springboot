package com.example.cloudgateway;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"uri.httpbin=http://localhost:${wiremock.server.port}"})
@AutoConfigureWireMock(port = 0)
class CloudGatewayApplicationTests {

    @LocalServerPort
    int port;
    @Autowired
    private WebTestClient webTestClient;
    private WebTestClient client;

    @BeforeEach
    public void setup() {
        client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

    @Test
    public void pathRouteWorks() {
        client.get().uri("/get")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(result -> {
                    Assertions.assertNotNull(result.getResponseBody());
                });
    }

    @Test
    public void hostRouteWorks() {
        client.get().uri("/headers")
                .header("Host", "www.myhost.org")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(result -> {
                    Assertions.assertNotNull(result.getResponseBody());
                });
    }

    @Test
    public void rewriteRouteWorks() {
        client.get().uri("/foo/get")
                .header("Host", "www.rewrite.org")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(result -> {
                    Assertions.assertNotNull(result.getResponseBody());
                });
    }

    @Test
    public void circuitBreakerRouteWorks() {
        client.get().uri("/delay/3")
                .header("Host", "www.circuitbreaker.org")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
    }

    @Test
    public void circuitBreakerFallbackRouteWorks() {
        client.get().uri("/delay/3")
                .header("Host", "www.circuitbreakerfallback.org")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("This is a fallback");
    }

    @Test
    public void rateLimiterWorks() {
        WebTestClient authClient = client.mutate()
                .filter(basicAuthentication("user", "password"))
                .build();

        boolean wasLimited = false;

        for (int i = 0; i < 20; i++) {
            FluxExchangeResult<Map> result = authClient.get()
                    .uri("/anything/1")
                    .header("Host", "www.limited.org")
                    .exchange()
                    .returnResult(Map.class);
            if (result.getStatus().equals(HttpStatus.TOO_MANY_REQUESTS)) {
                System.out.println("Received result: " + result);
                wasLimited = true;
                break;
            }
        }

        assertThat(wasLimited)
                .as("A HTTP 429 TOO_MANY_REQUESTS was not received")
                .isTrue();

    }

    @Test
    public void contextLoads() {
        // stubs
        stubFor(get(urlEqualTo("/get"))
                .willReturn(aResponse()
                        .withBody("{\"headers\":{\"Hello\":\"World\"}}")
                        .withHeader("Content-Type", "application/json")));
        stubFor(get(urlEqualTo("/delay/3"))
                .willReturn(aResponse()
                        .withBody("no fallback")
                        .withFixedDelay(3000)));

        webTestClient
                .get().uri("/get")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.headers.Hello").isEqualTo("World");

        webTestClient
                .get().uri("/delay/3")
                .header("Host", "www.circuitbreaker.com")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(
                        response -> assertThat(
                                response.getResponseBody()).isEqualTo("fallback".getBytes()));
    }

}
