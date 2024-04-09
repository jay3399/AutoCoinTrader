package com.example.autocointrader.infrastructure.api.exchange;


import com.example.autocointrader.application.ui.response.OrderChanceResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.validation.constraints.Min;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
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

        UriBuilder uriBuilder = UriComponentsBuilder.fromUriString(CHANCE_URL).queryParam("market", market);

        String uri = uriBuilder.build().toString();

        String token = generateAuthToken(uri.split("\\?")[1]);

        return webClient.get()
                .uri(uri)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(OrderChanceResponse.class);

    }

    public Mono<String> getOrderV2(String market, @Min(value = 5000, message = "최소 주문 금액은 5000원 이상이어야 합니다.") Double price, String volume, String side , String ordType) {



        Map<String, Object> body = new HashMap<>();
        body.put("market", market);
        body.put("side", side);
        body.put("ord_type", ordType); // 주문 유형

        switch (ordType) {
            case "price" -> {
                //bid -> 매수
                if ("bid".equals(side)) {
                    body.put("price", price); // 시장가 : 매수 금액 설정
                }
            }
            case "market" -> {
                //ask -> 매도
                if ("ask".equals(side)) {
                    body.put("volume", volume); // 시장가 : 매도 수량 설정
                }
            }
            case "limit" -> {
                // 지정가 주문: 가격과 수량 설정
                body.put("price", price);
                body.put("volume", volume);
            }
            default -> throw new IllegalArgumentException(ordType);
        }


        String queryString = createQueryString(body);

        String authToken = generateAuthToken(queryString);

        return webClient.post().uri("https://api.upbit.com/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authToken)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);

    }

    public Mono<String> getOrderV(String market, @Min(value = 5000, message = "최소 주문 금액은 5000원 이상이어야 합니다.") Double price, String volume, String side , String ordType) {

//        Mono<OrderChanceResponse> orderChance = getOrderChance(market);
//        OrderChanceResponse block = orderChance.block();
//        long balance = Long.parseLong(block.getBidAccount().getBalance());
//
//        if (price > balance) {
//            throw new IllegalArgumentException("금액부족");
//        }



        Map<String, Object> body = new HashMap<>();
        body.put("market", market);
        body.put("side", side);
        body.put("ord_type", ordType); // 주문 유형

        switch (ordType) {
            case "price" -> {
                //bid -> 매수
                if ("bid".equals(side)) {
                    body.put("price", price); // 시장가 : 매수 금액 설정
                }
            }
            case "market" -> {
                //ask -> 매도
                if ("ask".equals(side)) {
                    body.put("volume", volume); // 시장가 : 매도 수량 설정
                }
            }
            case "limit" -> {
                // 지정가 주문: 가격과 수량 설정
                body.put("price", price);
                body.put("volume", volume);
            }
            default -> throw new IllegalArgumentException(ordType);
        }


        String queryString = createQueryString(body);

        String authToken = generateAuthToken(queryString);


        return webClient.post().uri(ORDER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authToken)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);

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
