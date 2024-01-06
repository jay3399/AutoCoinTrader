package com.example.autocointrader.application.service;

import com.example.autocointrader.application.ui.response.OrderChanceResponse;
import com.example.autocointrader.domain.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderAppService {


    private final OrderService orderService;


    // 전량매도
    public Mono<String> sellAllCoins(String market) {

        return orderService.getOrderChance(market).flatMap(
                chance -> {
                    String balance = chance.getAskAccount().getBalance();
                    System.out.println("balance = " + balance);
                    return orderService.getOrderV2(market, null, balance, "ask", "market");
                });
    }


    //전량매수
    public Mono<String> buyAllCoins(String market) {

        return orderService.getOrderChance(market).flatMap(

                chance -> {
                    String price = chance.getBidAccount().getBalance();
                    return orderService.getOrderV2(market, price, null, "bid", "price");

                }
        );


    }

    private String getCoins(String market) {

        Mono<OrderChanceResponse> orderChance = orderService.getOrderChance(market);

        return orderChance.block().getBidAccount().getBalance();
    }

    private String getPrice(String market) {

        Mono<OrderChanceResponse> orderChance = orderService.getOrderChance(market);

        return orderChance.block().getAskAccount().getBalance();

    }


}
