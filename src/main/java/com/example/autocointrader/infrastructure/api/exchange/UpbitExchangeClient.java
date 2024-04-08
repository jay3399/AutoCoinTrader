package com.example.autocointrader.infrastructure.api.exchange;


import com.example.autocointrader.application.ui.response.OrderChanceResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Component
public class UpbitExchangeClient {

    private final WebClient webClient;
    public final String accessKey;
    public final String secretKey;

    private static final String ORDER_URL = "/v1/orders";
    private static final String ACCOUNT_URL = "/v1/accounts";

    private static final String CHANCE_URL = "/v1/chance";


    public UpbitExchangeClient(@Value("${upbit.access-key}") String accessKey,
            @Value("${upbit.secret-key}") String secretKey,
            @Value("${upbit.base-url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }



    public Mono<OrderChanceResponse> getOrderChance(String market) {

        String queryString = "market=" + market;

        String token = generateAuthToken(queryString);

        return webClient.get()
                .uri("https://api.upbit.com/v1/orders/chance?market=" + market)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(OrderChanceResponse.class);

    }

    public Mono<OrderChanceResponse> getOrderChanceV(String market) {

        UriBuilder uriBuilder = UriComponentsBuilder.fromUriString(CHANCE_URL);

        uriBuilder.queryParam("market", market);

        String uri = uriBuilder.build().toString();

        String queryString = "market=" + market;

        String token = generateAuthToken(queryString);

        return webClient.get()
                .uri(uri)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(OrderChanceResponse.class);

    }



    private String generateAuthToken(String queryString) {

        String nonce = UUID.randomUUID().toString();
        String queryHash = "";
        String queryHashAlg = "SHA512";


        if (queryString != null && !queryString.isEmpty()) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-512");
                md.update(queryString.getBytes("UTF-8"));
                byte[] digest = md.digest();
                queryHash = String.format("%0128x", new BigInteger(1, digest));
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }


        String jwtToken = Jwts.builder()
                .setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT")
                .claim("access_key", accessKey)
                .claim("nonce", nonce)
                .claim("query_hash", queryHash)
                .claim("query_hash_alg", queryHashAlg)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();

        return "Bearer " + jwtToken;

    }






}
