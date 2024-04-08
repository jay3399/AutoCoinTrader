package com.example.autocointrader.infrastructure.api.quotation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class UpbitQuotationClient {

    private final WebClient webClient;
    public final String accessKey;
    public final String secretKey;

    private static final String MARKET_DATA_URL = "/v1/ticker";

    public UpbitQuotationClient(@Value("${upbit.access-key}") String accessKey, @Value("${upbit.secret-key}")  String secretKey , @Value("${upbit.base-url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }






}
