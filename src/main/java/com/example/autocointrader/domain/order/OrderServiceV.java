package com.example.autocointrader.domain.order;


import com.example.autocointrader.application.ui.response.OrderChanceResponse;
import com.example.autocointrader.infrastructure.api.exchange.UpbitExchangeClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderServiceV {

    private final UpbitExchangeClient upbitExchangeClient;


    public Mono<String> getOrder(Order order) {

        return upbitExchangeClient.getOrderV(order);

    }


    public Mono<OrderChanceResponse> getOrderChance(String market) {

        return upbitExchangeClient.getOrderChanceV(market);

    }





}
