package com.example.autocointrader.domain.account;

import com.example.autocointrader.application.ui.response.AccountBalance;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AccountService {

    private final WebClient webClient = WebClient.create();
    public final String accessKey;
    public final String secretKey;


    public AccountService(@Value("${upbit.access-key}") String accessKey, @Value("${upbit.secret-key}")  String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }


    public Flux<AccountBalance> getAllAccounts() {

        String token = getToken();

        return webClient.get().uri("https://api.upbit.com/v1/accounts")
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToFlux(AccountBalance.class);
    }


    public Mono<Double> getAvailableBalance(String currency) {

        String token = getToken();

        return webClient.get().uri("https://api.upbit.com/v1/accounts")
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToFlux(AccountBalance.class)
                .filter(account -> account.getCurrency().equals(currency))
                .map(account -> account.getBalance() - account.getLocked())
                .next();
    }





    private String getToken() {
        String nonce = UUID.randomUUID().toString();

        String token = Jwts.builder().setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT")
                .claim("access_key", accessKey).claim("nonce", nonce)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes()).compact();
        return token;
    }


}
