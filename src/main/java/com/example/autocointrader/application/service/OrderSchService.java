package com.example.autocointrader.application.service;


import com.example.autocointrader.application.ui.response.CoinDataResponse;
import com.example.autocointrader.domain.manager.buy.PurchaseManager;
import com.example.autocointrader.domain.order.OrderService;
import com.example.autocointrader.infrastructure.api.MarketDataClient;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderSchService {


    /**
     * 조건에 맞는코인이 있는지체 주기적으로 체크 후 매수 시작 ( 매수를 시작했을시에는 , 추가매수 종료시까지 조건 체크안한다 )
     * <p>
     * 여기서는 , 여러 코인을  매수 가능 . ( + 최대 개수 제한을 두어야 할것같음 )
     * <p>
     * + (매수 시작한 , 코인리스트를 캐시서버에 저장을 해야할수도 있을것같음 , 분할매수를 생각해서 )
     * <p>
     * 코인은 매수시 한번 매수하고 끝나는것이 아닌 ,특정시간동안 ( 특정 간격마다 조건 체크 ) 조건 만족시 최대 3번까지 분할매수를 진행한다
     * <p>
     * 처음 매수할떄는  , 계좌 잔고의 20퍼센트 잔고만큼을 해당 코인매수에 할당하고 , 첫 매수때는 해당 금액의 절반만을 시장가로 매수한다
     * <p>
     * 그 이후 , 해당 코인을 특정 시간동안 체크하면서 내가 매수했던 가격 보다 해당 코인의 현재 가격이 더 낮아 졌을시에 해당코인을 매수하기로 할당한 남은금액의 절반을
     * 추가매수
     * <p>
     * 그 이후 , 이 직전 매수가격보다 현재 가격이 또 낮아졌을시에 해당 코인에 할당한 금액 모두 매수
     * <p>
     * 예를들어) 특정조건을 만족하는 코인 a 와 ,b 를 발견 현재 매수에 쓸수있는 계좌의 원화(KRW)가 100만원 있다 가정시 , 100만원의 20프로인 20만원을 a
     * 매수하는데 사용  , 남은 80만원의 20프로인 16만원을 b를 매수하는데 사용 .
     * <p>
     * 처음에는 일단 시장가로 a 를 시장가로 20만원의 50프로인 10만원을 매수 , b또한 16만원의 50프로인 8만원을 매수
     * <p>
     * 그리고 a , b 를 5분뒤 다시 현재 가격 체크후  , 직전에 매수한 가격보다 현재가격이 낮을경우 매수 , 남은 금액의 50프로만큼매수 -> a는 5만원 , b 는
     * 4만원 ( 체크했을떄 가격이 현재가격이 더 높은경우에는 추가매수 종료 )
     * <p>
     * 이전에 추가매수를 실행한경우  , 5분뒤 현재 가격체크 후 직전 매수한 가격보다 또 낮아졌다면 남은 금액 모두 매수 , a는 5만원 , b는 4만원  ( 체크했을떄
     * 가격이 현재가격보다 높을경우 추가매수 종료 )
     * <p>
     * <p>
     * 위 매수 과정이 모두 끝이나면 , 그떄부터 다시 조건이 맞는 코인이 있는지 체크시작 .. 반복 ~
     */


    private final OrderService orderService;

    private final MarketDataClient marketDataClient;

    private final Map<String, PurchaseManager> activePurchases = new ConcurrentHashMap<>();
    private final AtomicBoolean isPurchasing = new AtomicBoolean(false);


    public void executeOrders() {

        Flux.interval(Duration.ofMinutes(1))
                .filter(s -> !isPurchasing.get())
                .flatMap(t -> marketDataClient.getAllMarketCodes())
                .filterWhen(this::shouldPurchase)
                .flatMap(this::initPurchase)
                .subscribe();

    }

    // 조건 체크 로직
    private Mono<Boolean> shouldPurchase(String market) {

        if (activePurchases.containsKey(market)) {
            return Mono.just(false);
        }

        return marketDataClient.getMarketData(market).map(
                //ex
                marketData -> {
                    double price = 100;
                    return marketData.getCurrentPrice() < price;
                }
        ).defaultIfEmpty(false);
    }


    // 초기화
    private Mono<PurchaseManager> initPurchase(String market) {
        return orderService.getAvailableBalance("KRW").map(
                balance -> {
                    double amount = balance * 0.2;
                    PurchaseManager purchaseManager = PurchaseManager.create(market, amount);
                    activePurchases.put(market, purchaseManager);
                    isPurchasing.set(true);

                    executePurchase(purchaseManager).subscribe();

                    return purchaseManager;
                }
        );
    }

    // 분할 매수 로직
    private Mono<String> executePurchase(PurchaseManager manager) {

        return Flux.interval(Duration.ofMinutes(5))
                .take(3)
                .flatMap(s -> marketDataClient.getMarketData(manager.getMarket()))
                .filter(marketData -> executeAdditionalPurchase(manager, marketData))
                .flatMap(marketData -> {

                    double amount = manager.calculateRemainingAmount();

                    return orderService.getOrder(manager.getMarket(), amount).doOnNext(
                            response -> {
                                manager.updatePurchaseManager(marketData.getCurrentPrice(), amount);

                                if (manager.getPurchaseCount() >= 3) {
                                    completePurchase(manager);
                                }

                            });
                })
                .next()
                .doOnSuccess(response -> {
                    if (manager.getPurchaseCount() >= 3) {
                        completePurchase(manager);
                    }
                })
                .doFinally(s -> {
                    if (activePurchases.isEmpty()) {
                        isPurchasing.set(false);
                    }
                });


    }

    private void completePurchase(PurchaseManager manager) {
        activePurchases.remove(manager.getMarket());
        if (activePurchases.isEmpty()) {
            isPurchasing.set(false);
        }
    }


    private boolean executeAdditionalPurchase(PurchaseManager manager,
            CoinDataResponse marketData) {

        return manager.getPurchaseCount() == 0
                || marketData.getCurrentPrice() < manager.getLastPurchasePrice();

    }


}
