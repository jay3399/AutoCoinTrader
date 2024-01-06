package com.example.autocointrader.infrastructure.api;

import com.example.autocointrader.application.ui.response.CoinDataResponse;
import com.example.autocointrader.application.ui.response.MarketCodeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MarketDataClient {

    private final WebClient webClient = WebClient.create();



    public Flux<String> getAllMarketCodes() {

        return webClient.get().uri("https://api.upbit.com/v1/market/all")
                .retrieve().bodyToFlux(MarketCodeResponse.class)
                .map(MarketCodeResponse::getMarket)
                .filter(market -> market.startsWith("KRW-"));
    }


    public Mono<CoinDataResponse> getMarketData(String marketCode) {

        return webClient.get()
                .uri("https://api.upbit.com/v1/ticker?markets=" + marketCode)
                .retrieve()
                .bodyToFlux(CoinDataResponse.class)
                .next();

    }




}
