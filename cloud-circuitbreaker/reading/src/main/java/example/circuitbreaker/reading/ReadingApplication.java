package example.circuitbreaker.reading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@SpringBootApplication
@ConfigurationPropertiesScan
public class ReadingApplication {

    private final BookService bookService;

    public ReadingApplication(BookService bookService) {
        this.bookService = bookService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ReadingApplication.class, args);
    }

    @RequestMapping("/to-read")
    public Mono<String> toRead() {
        return bookService.readingList();
    }
}
