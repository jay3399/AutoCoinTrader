package com.example.autocointrader.application.service;

import com.example.autocointrader.domain.order.Order;
import com.example.autocointrader.domain.order.OrderBuyService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderMarketService {

    private final OrderBuyService orderBuyService;


    public Mono<String> placeMarketOrder(Order order) {

        return orderBuyService.getBalanceForBuying(order.getMarket()).flatMap(balance -> order.checkBalance(balance).flatMap(isEnough ->
        {
            if (isEnough) {
                return orderBuyService.getOrderForBuying(order);
            } else {
                return Mono.error(new RuntimeException("잔액 부족"));
            }
        }));


    }



}
