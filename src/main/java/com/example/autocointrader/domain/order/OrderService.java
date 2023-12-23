package com.example.autocointrader.domain.order;

import com.example.autocointrader.application.ui.response.AccountBalance;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
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

        String queryString = createQueryString(body);

        String authToken = generateAuthToken(queryString);



        return webClient.post().uri("https://api.upbit.com/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authToken)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);

    }


    public Mono<Double> getAvailableBalance(String currency) {

        Map<String, Object> body = new HashMap<>();

        String queryString = createQueryString(body);

        String token = generateAuthToken(queryString);

        return webClient.get().uri("https://api.upbit.com/v1/accounts")
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToFlux(AccountBalance.class)
                .filter(account -> account.getCurrency().equals(currency))
                .map(account -> account.getBalance() - account.getLocked())
                .next();


    }

    private String createQueryString(Map<String, Object> params) {
        return params.entrySet().stream()
                .map(s -> s.getKey() + "=" + s.getValue())
                .collect(Collectors.joining("&"));
    }



    private String generateAuthToken(String queryString) {

        String nonce = UUID.randomUUID().toString();
        String queryHash = "";
        String queryHashAlg = "SHA512";

        if (queryString != null && !queryHashAlg.isEmpty()) {

            try {
                MessageDigest md = MessageDigest.getInstance("SHA-512");
                byte[] digest = md.digest(queryString.getBytes("UTF-8"));
                StringBuilder sb = new StringBuilder();

                for (byte b : digest) {
                    sb.append(String.format("%02x", b));
                }

                queryHash = sb.toString();

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

        return "Bearer" + jwtToken;

    }



}
