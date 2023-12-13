package com.example.autocointrader.infrastructure.api;

import com.example.autocointrader.application.ui.response.MarketResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class MarketDataClient {

    private final WebClient webClient = WebClient.create();

    private static final String URL = "https://api.upbit.com/v1/market/all";


    public Flux<String> getAllMarketCodes() {

        return webClient.get().uri(URL).retrieve().
                bodyToFlux(MarketResponse.class)
                .map(MarketResponse::getMarket)
                .filter(market -> market.startsWith("KRW-"));
    }




}
