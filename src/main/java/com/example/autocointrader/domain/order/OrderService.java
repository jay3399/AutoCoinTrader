package com.example.autocointrader.domain.order;

import com.example.autocointrader.application.ui.response.AccountBalance;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
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

    public Mono<String> getOrder(String market, double volume) {

        Map<String, Object> body = new HashMap<>();
        body.put("market", market);
        body.put("side", "bid");
        body.put("volume", String.valueOf(volume));

        //시장가 구매
        body.put("ord_type", "price");

        String authToken = generateAuthToken(body);

        return webClient.post().uri("https://api.upbit.com/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authToken)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);

    }


    public Mono<Double> getAvailableBalance(String currency) {

        Map<String, Object> body = new HashMap<>();

        String token = generateAuthToken(body);

        return webClient.get().uri("https://api.upbit.com/v1/accounts")
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToFlux(AccountBalance.class)
                .filter(account -> account.getCurrency().equals(currency))
                .map(account -> account.getBalance() - account.getLocked())
                .next();


    }

    private String generateAuthToken(Map<String , Object> body) {

        String token = null;

        return "Bearer" + token;
    }


}
