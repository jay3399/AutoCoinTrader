package com.example.autocointrader.domain.order;

import com.example.autocointrader.application.ui.response.OrderChanceResponse;
import com.example.autocointrader.application.ui.response.OrderChanceResponse.AccountInfo;
import com.example.autocointrader.infrastructure.api.exchange.UpbitExchangeClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderBuyService {

    private final UpbitExchangeClient upbitExchangeClient;


    /**
     *
     * @param
     * @return 해당 마켓 매수가능 잔고
     */
    public Mono<Double> getBalanceForBuying(String market) {

        return upbitExchangeClient.getOrderChanceV(market).map(OrderChanceResponse::getAskAccount)
                .map(AccountInfo::getBalance).map(Double::parseDouble);


    }


    public Mono<String> getOrderForBuying(Order order) {

        return upbitExchangeClient.getOrderV(order).map(res -> "주문성공 : OrderId :..")
                .onErrorResume(e -> Mono.error(new RuntimeException("주문실패")));



    }








}
//    public Mono<String> getBalanceForBuying(Order order) {
//
//        return upbitExchangeClient.getOrderChanceV(order.getMarket())
//                .map(OrderChanceResponse::getAskAccount).map(AccountInfo::getBalance)
//                .map(Double::parseDouble)
//                .map(balance -> order.checkBalance(balance)).flatMap(this::getOrderForBuying)
//                .onErrorResume(e -> Mono.just(e.getMessage()));
//
//    }
