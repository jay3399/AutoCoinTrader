package com.example.autocointrader;

import com.example.autocointrader.infrastructure.api.MarketDataClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
@Slf4j
public class AutoCoinTraderApplication {

//    private static final String URL = "https://api.upbit.com/v1/market/all";

    public static void main(String[] args) {

        SpringApplication.run(AutoCoinTraderApplication.class, args);

        MarketDataClient marketDataClient = new MarketDataClient();

        marketDataClient.getAllMarketCodes().subscribe(marketCode -> log.info("Market Code: {}", marketCode));




    }

}
