package com.example.autocointrader.domain.order;

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
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;



@Service
public class OrderService {

    private final WebClient webClient = WebClient.create();
    public final String accessKey;
    public final String secretKey;

    public OrderService(@Value("${upbit.access-key}") String accessKey, @Value("${upbit.secret-key}")  String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }


    /**
     * 클라이언트 ( 마켓 , 매수 or 매도 ( 가격 , 볼륨 )  , 시장가매수(매수금액만) or 시장가매도(매도수량만) ) ->  주문생성  ( price 5000 이하 x , price 가 balance 를 초과할떄도 x )
     * price 5000이하는 아래와같이 파라미터 검증단에서 막았다.
     * 1.하지만 price 의 balance 초과부분은 어떻게 ? 직접 만든다 vs api 주문가능한금액 기능을 사용한다
     * 2.검증부분을 클라이언트 요청을 처음받는 controller단에서 모두 처리하는게 괜찮을까 ? 아니면 아래와같이 서비스에서 처리할까.
     *
     * controller 나눈다 . ?
     *
     * 매수전용 -> 1.시장가매수 ,2.지정가매수
     *
     * 메도전용 -> 1.시장가매도 ,2.지정가매도
     *
     *
     */

    public Mono<String> getOrderV2(String market, @Min(value = 5000, message = "최소 주문 금액은 5000원 이상이어야 합니다.") Double price, String volume, String side , String ordType) {

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

        return webClient.post().uri("https://api.upbit.com/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authToken)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);

    }

    public Mono<String> getOrderV1(String market, double price, String volume, String side) {

        Map<String, Object> body = new HashMap<>();
        body.put("market", market);
        body.put("side", side);
        body.put("price", price);

        // 매수는 ( "bid" )  ,시장가시 volume 필요없음
        // 매도는 ( "ask" ) , 시장가시 price  필요없음
        body.put("volume", volume);

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


    public Mono<OrderChanceResponse> getOrderChance(String market) {

        String queryString = "market=" + market;

        String token = generateAuthToken(queryString);

        return webClient.get()
                .uri("https://api.upbit.com/v1/orders/chance?market=" + market)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(OrderChanceResponse.class);

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
