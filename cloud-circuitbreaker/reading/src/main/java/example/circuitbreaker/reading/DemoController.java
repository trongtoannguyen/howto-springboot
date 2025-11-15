package example.circuitbreaker.reading;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class DemoController {


    private final HttpBinService httpBin;
    private final BookService bookService;

    public DemoController(HttpBinService httpBin, BookService bookService) {
        this.httpBin = httpBin;
        this.bookService = bookService;
    }

    @RequestMapping("/to-read")
    public Mono<String> toRead() {
        return bookService.readingList();
    }

    //region HttpBin endpoints
    @GetMapping("/get")
    public Mono<Map> get() {
        return httpBin.get();
    }

    @GetMapping("/delay/{seconds}")
    public Mono<Map> delay(@PathVariable int seconds) {
        return httpBin.delay(seconds);
    }

    @GetMapping("/fluxdelay/{seconds}")
    public Flux<String> fluxDelay(@PathVariable int seconds) {
        return httpBin.fluxDelay(seconds);
    }

    //endregion
}
