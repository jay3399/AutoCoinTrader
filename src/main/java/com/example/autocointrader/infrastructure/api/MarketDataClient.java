package com.example.autocointrader.infrastructure.api;

import com.example.autocointrader.application.ui.response.CoinDataResponse;
import com.example.autocointrader.application.ui.response.MarketCodeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarketDataClient {

    private final WebClient webClient = WebClient.create();



    public Flux<String> getAllMarketCodes() {

//        long start = System.currentTimeMillis();


        log.info("thread: {}", Thread.currentThread().getName());

        Flux<String> filter = webClient.get().uri("https://api.upbit.com/v1/market/all")
                .retrieve().bodyToFlux(MarketCodeResponse.class)
                .map(MarketCodeResponse::getMarket)
                .filter(market -> market.startsWith("KRW-"))
                .doOnSubscribe(
                        s -> log.info("start on thread: {}", Thread.currentThread().getName()))
                .doOnNext(code -> log.info("market code: {} on thread: {}", code,
                        Thread.currentThread().getName()
                )).doOnComplete(() -> log.info("completed on thread:{}", Thread.currentThread().getName()));

//
//        long end = System.currentTimeMillis();
//
//        log.info("thread: {} , time : {} ms", Thread.currentThread().getName(), end - start);
//
//        try {
//            Thread.sleep(5000);
//            log.info("Main thread: {}", Thread.currentThread().getName());
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//


        return filter;


    }


    public Mono<CoinDataResponse> getMarketData(String marketCode) {

        return webClient.get()
                .uri("https://api.upbit.com/v1/ticker?markets=" + marketCode)
                .retrieve()
                .bodyToFlux(CoinDataResponse.class)
                .next();

    }




}
