package com.example.autocointrader.application.ui.controller.service;


import com.example.autocointrader.domain.manager.buy.BuyStrategy;
import com.example.autocointrader.domain.order.OrderService;
import com.example.autocointrader.infrastructure.api.MarketDataClient;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class OrderSchService {


    //매 간격별로 모든 코인리스트의 조건 체크후. (checkCondition)

    // 특정 조건이 만족되면 . 해당코인(market) 매수 시작


    // 현재 계좌의 잔액을 가져온뒤 (currentBalance) by API

    // 해당 잔액의 일정 비율만큼을 ( orderBalance ) 해당 코인 매수시작

    // 1. orderBalance 의 절반 금액을 ( /2 )  시장가 매수

    // 2. 매수후 특정 시간동안 주기적으로 가격을 체크

    // 3. 특정시간내 가격을 체크하였을떄  ,1. 시장가 매수한 가격보다 가격이 낮을시에 orderBalance 의 2/3 만큼을 시장가매수 하고  2. 또 체크하였을때 이직전 시장가 매수했을때보다 낮을경우 남은 orderBalance 전체 매수 후 매수종료

    // 4. 특정시간내 직전 시장가 메수보다 계속 높을경우 매수하지않고 , 시간이 만료될시 해당 코인 매수 종료 .



    private final OrderService orderService;

    private final MarketDataClient marketDataClient;

    private final BuyStrategy buyStrategy;


    public void checkCondition() {

        Flux.interval(
                        Duration.ofMinutes(1)
                ).flatMap(s -> marketDataClient.getAllMarketCodes())
                .flatMap(marketCode -> marketDataClient.getMarketData(marketCode))
                .filter(buyStrategy::shouldBuy)
                .flatMap(coinData -> executeOrder(coinData.getSymbol()))
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();

    }


//    public Flux<String> executeOrder(String market) {
//
//       return orderService.getAvailableBalance("KRW")
//                .flatMapMany(
//                        availableBalance -> {
//
//                        }
//
//
//                )
//
//
//
//
//
//    }


}
