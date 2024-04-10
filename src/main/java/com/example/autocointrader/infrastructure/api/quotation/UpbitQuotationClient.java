package com.example.autocointrader.infrastructure.api.quotation;

import com.example.autocointrader.application.ui.response.CoinDataResponse;
import com.example.autocointrader.application.ui.response.MarketCodeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class UpbitQuotationClient {

    private final WebClient webClient;
    public final String accessKey;
    public final String secretKey;

    private static final String MARKET_DATA_URL = "/v1/ticker";
    private static final String MARKET_CODE_URL = "/market/all";


    public UpbitQuotationClient(@Value("${upbit.access-key}") String accessKey, @Value("${upbit.secret-key}")  String secretKey , @Value("${upbit.base-url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }


    public Mono<CoinDataResponse> getMarketData(String marketCode) {

        UriBuilder uriBuilder = UriComponentsBuilder.fromUriString(MARKET_DATA_URL).queryParam("market", marketCode);

        String uri = uriBuilder.build().toString();

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToFlux(CoinDataResponse.class)
                .next();
    }


    public Flux<String> getAllMarketCodes() {

        return webClient.get().uri(MARKET_CODE_URL).retrieve().bodyToFlux(MarketCodeResponse.class)
                .map(MarketCodeResponse::getMarket).filter(market -> market.startsWith("KRW-"));



    }












}
