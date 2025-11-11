package com.example.cloudgateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"uri.httpbin=http://localhost:${wiremock.server.port}"})
@AutoConfigureWireMock(port = 0)
class CloudGatewayApplicationTests {

    @LocalServerPort
    int port;

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private WebSocketClient webSocketClient;

    @BeforeEach
    public void setup() {
        webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

    @Test
    public void pathRouteWorks() {
        String body = "{\"headers\":{\"Host\":\"httpbin.org\"}}";

        stubFor(get(urlEqualTo("/get"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(body)));

        webTestClient.get().uri("/get")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-Response-Default-Foo", "Default-Bar")
                .expectBody()
                .json(body)
                .consumeWith(result -> assertThat(result.getResponseBody()).isNotEmpty());
    }

    @Test
    public void hostRouteWorks() {
        stubFor(get(urlEqualTo("/headers"))
                .willReturn(aResponse()
                        .withStatus(200)));

        webTestClient.get().uri("/headers")
                .header("Host", "www.myhost.org")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-Response-Foo", "MyHost")
                .expectBody();
    }

    @Test
    public void rewriteRouteWorks() {
        String body = "{\"cookies\":{}}";
        stubFor(get(urlEqualTo("/cookies"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(body)));

        webTestClient.get().uri("/foo/cookies")
                .header("Host", "www.rewrite.org")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-Rewrite-Response", "Rewritten")
                .expectBody()
                .consumeWith(result -> assertThat(result.getResponseBody()).isNotEmpty());
    }

    @Test
    public void circuitBreakerRouteWorks() {
        stubFor(get(urlEqualTo("/delay/3"))
                .willReturn(aResponse()
                        .withBody("no fallback")
                        .withStatus(HttpStatus.GATEWAY_TIMEOUT.value())
                        .withFixedDelay(3000)));

        webTestClient.get().uri("/delay/3")
                .header("Host", "www.circuitbreaker.org")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
    }

    @Test
    public void circuitBreakerFallbackRouteWorks() {
        stubFor(get(urlEqualTo("/delay/3"))
                .willReturn(aResponse().withFixedDelay(3000)));

        webTestClient.get().uri("/delay/3")
                .header("Host", "www.circuitbreakerfallback.org")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("call me a callback");

    }

    @Test
    public void circuitBreakerFallbackRouteWorks2() {
        stubFor(get(urlEqualTo("/delay/3"))
                .willReturn(aResponse().withBody("fallback")));

        webTestClient.get().uri("/delay/3")
                .header("Host", "www.circuitbreakerfallback.org")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-CircuitBreaker", "NotTriggered");

    }

    @Test
    public void rateLimiterWorks() {
        stubFor(get(urlEqualTo("/anything/redis"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"result\":\"success\"}")));

        var authClient = webTestClient.mutate()
                .filter(basicAuthentication("user", "password"))
                .build();
        boolean wasLimited = false;

        for (int i = 0; i < 20; i++) {
            FluxExchangeResult<Map> result = authClient.get()
                    .uri("/anything/redis")
                    .header("Host", "www.limited.org")
                    .exchange()
                    .returnResult(Map.class);

            result.getResponseBody().blockFirst();

            if (result.getStatus().equals(HttpStatus.TOO_MANY_REQUESTS)) {
                System.out.println("Received result: " + result);
                wasLimited = true;
                break;
            }
        }

        assertThat(wasLimited)
                .as("A HTTP 429 TOO_MANY_REQUESTS was received")
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
                .expectHeader().valueEquals("Halo", "Foobar")
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
