package example.circuitbreaker.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@SpringBootApplication
public class BookstoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
    }

    @RequestMapping("/recommended")
    public Mono<String> readingList() {
        return Mono.just("Cloud Native Java, Kubernetes in Action, Spring Boot in Action");
    }
}
