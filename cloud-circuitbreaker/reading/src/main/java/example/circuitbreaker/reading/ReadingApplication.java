package example.circuitbreaker.reading;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@SpringBootApplication
public class ReadingApplication {

    private final WebClient webClient;

    @Value(value = "${uri.bookstore:http://localhost:8090}")
    private String bookstoreUri = "";

    public ReadingApplication(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(ReadingApplication.class, args);
    }

    @RequestMapping("/to-read")
    public Mono<String> toRead() {
        return webClient.get().uri(bookstoreUri)
                .retrieve()
                .bodyToMono(String.class);
    }
}
