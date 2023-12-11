package com.example.autocointrader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class AutoCoinTraderApplication {

    private static final String URL = "https://api.upbit.com/v1/market/all";

    public static void main(String[] args) {
        SpringApplication.run(AutoCoinTraderApplication.class, args);

        WebClient webClient = WebClient.create();
        Mono<String> response = webClient.get().uri(URL).retrieve().bodyToMono(String.class);
        response.subscribe(System.out::println);


    }

}
