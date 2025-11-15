package example.circuitbreaker.reading;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);
    private final ReactiveCircuitBreaker readingListCircuitBreaker;
    private final WebClient webClient;

    public BookService(ReactiveCircuitBreakerFactory<?, ?> reactiveCircuitBreakerFactory,
                       WebClient.Builder webClientBuilder, UriConfiguration uriConfiguration) {
        String storeUri = Objects.requireNonNull(uriConfiguration.getBookstore(), "Bookstore URI cannot be null");
        this.readingListCircuitBreaker = reactiveCircuitBreakerFactory.create("recommended");
        this.webClient = webClientBuilder.baseUrl(storeUri).build();
    }

    public Mono<String> readingList() {
        return readingListCircuitBreaker.run(
                webClient.get().uri("/recommended").retrieve().bodyToMono(String.class)
                , throwable -> {
                    log.warn("error making request to book service", throwable);
                    return Mono.just("Cloud native Java");
                });
    }
}
