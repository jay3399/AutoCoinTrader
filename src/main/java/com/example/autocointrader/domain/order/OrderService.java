package com.example.autocointrader.domain.order;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class OrderService {

    private final WebClient webClient = WebClient.create();
    private final String accessKey;
    private final String secretKey;

    public OrderService(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public Mono<String> buyOrder(String market, double volume) {

        Map<String, Object> body = new HashMap<>();
        body.put("market", market);
        body.put("side", "bid");
        body.put("volume", String.valueOf(volume));
        body.put("ord_type", "price");

        String authToken = generateAuthToken(body);

        return webClient.post().uri("https://api.upbit.com/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authToken)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);

    }

    private String generateAuthToken(Map<String, Object> body) {

        String token = null;

        return "Bearer" + token;
    }


}
