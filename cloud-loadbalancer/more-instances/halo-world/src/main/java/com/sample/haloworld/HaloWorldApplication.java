package com.sample.haloworld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController
@SpringBootApplication
public class HaloWorldApplication {

    private static final Logger log = LoggerFactory.getLogger(HaloWorldApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(HaloWorldApplication.class, args);
    }

    @GetMapping("/greeting")
    public String greet() {
        log.info("Access /greeting");
        List<String> greetings = List.of("Hello", "Hola", "Bonjour", "Ciao", "Hallo");
        Random random = new Random();
        var randNum = random.nextInt(greetings.size());
        return greetings.get(randNum);
    }

    @GetMapping
    public String home(){
        log.info("Access /");
        return "Halo";
    }

}
