package example.circuitbreaker.reading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Toan Nguyen
 */
@Service
public class HttpBinService {

    private static final Logger log = LoggerFactory.getLogger(HttpBinService.class);
    private final WebClient webClient;
    private final ReactiveCircuitBreaker httpBinReactiveCircuitBreaker;

    public HttpBinService(WebClient.Builder webClientBuilder, ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory, UriConfiguration uriConfiguration) {
        String httpBinUrl = uriConfiguration.getHttpbin();
        this.webClient = webClientBuilder.baseUrl(httpBinUrl).build();
        this.httpBinReactiveCircuitBreaker = reactiveCircuitBreakerFactory.create("delay");
    }

    public Mono<Map> get() {
        return webClient.get().uri("/get").retrieve().bodyToMono(Map.class);
    }

    /**
     * Delay response by certain seconds
     */
    public Mono<Map> delay(int seconds) {
        return httpBinReactiveCircuitBreaker.run(
                webClient.get().uri("/delay/{seconds}", seconds).retrieve().bodyToMono(Map.class),
                throwable -> {
                    log.warn("delay call failed error", throwable);
                    return Mono.just(Map.of("hola", "world"));
                }
        );
    }

    /**
     * Delay response by certain seconds - return a Supplier of Mono
     */
    public Supplier<Mono<Map>> delaySupplier(int seconds) {
        return () -> httpBinReactiveCircuitBreaker.run(
                this.delay(seconds), t -> {
                    log.warn("delay call failed error");
                    Map<String, String> fallback = new HashMap<>();
                    fallback.put("hello", "world");
                    return Mono.just(fallback);
                }
        );
    }

    /**
     * Flux that emits 3 values with delay between each element
     */
    public Flux<String> fluxDelay(int seconds) {
        return httpBinReactiveCircuitBreaker.run(
                Flux.just("1", "2", "3").delayElements(Duration.ofSeconds(seconds)),
                t -> {
                    log.warn("delay call failed error", t);
                    return Flux.just("hello", "world");
                }
        );
    }

    //region fallback method
    private Mono<Map> fallback(int seconds, Throwable throwable) {
        log.warn("delay call failed error", throwable);
        Map<String, String> fallback = new HashMap<>();
        fallback.put("hello", "world");
        return Mono.just(fallback);
    }
    //endregion
}
