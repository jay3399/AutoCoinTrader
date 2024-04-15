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
     * @param marketCode
     * @return 해당 마켓 매수가능 잔고
     */
    public Mono<String> getBalanceForBuying(String marketCode) {

        Mono<String> balance = upbitExchangeClient.getOrderChanceV(marketCode)
                .map(OrderChanceResponse::getAskAccount).map(AccountInfo::getBalance);

        return balance;
    }


//    public Mono<String> getOrderForBuying(Order order) {
//
//        Mono<String> balanceForBuying = getBalanceForBuying(order.getMarket());
//
//
//
//
//
//
//    }









}
