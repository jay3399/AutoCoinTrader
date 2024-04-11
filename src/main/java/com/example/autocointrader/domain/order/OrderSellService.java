package com.example.autocointrader.domain.order;


import com.example.autocointrader.application.ui.response.OrderChanceResponse;
import com.example.autocointrader.application.ui.response.OrderChanceResponse.AccountInfo;
import com.example.autocointrader.infrastructure.api.exchange.UpbitExchangeClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderSellService {

    private final UpbitExchangeClient upbitExchangeClient;


    /**
     * @param marketCode
     * @return 해당마켓 매도가능 수량
     */
    public Mono<String> getQuantityForSelling(String marketCode) {

        Mono<String> quantity = upbitExchangeClient.getOrderChanceV(marketCode)
                .map(OrderChanceResponse::getAskAccount).map(
                        AccountInfo::getBalance);

        return quantity;

    }

}
