package com.example.autocointrader.application.service;


import com.example.autocointrader.application.ui.response.CoinDataResponse;
import com.example.autocointrader.domain.account.AccountService;
import com.example.autocointrader.domain.manager.buy.PurchaseManager;
import com.example.autocointrader.domain.order.OrderService;
import com.example.autocointrader.infrastructure.api.MarketDataClient;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderSchService {

    /** 자동매수
     *
     * 시나리오
     *
     * 1.첫번째
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
     *
     *
     * 2. 두번째 시나리오
     *
     * 위와같이 , 모든 코인을 탐색하지않고
     *
     * 특정코인을 내가 지정하고 , 그것만을 체크한다 ( 파라미터로 특정코인의 마켓코드와 해당하는 지정가격을 보낸다  , 해당 부분은 한개가 될수도있고 여러 코인이 될수도 있다 . )
     *
     * 조건은 위에서 지정한 해당 가격에 도달하면 해당 가격에서 부터 매수를 시작한다.
     *
     * 분할 매수전략은 위와 동일하다 .
     *
     */

    /**
     * 자동 매도 - 주문메서드 (orderService) , 내가 현재 갖고있는 코인리스트
     *
     *
     *
     */


    /**
     *
     */


    private final OrderService orderService;

    private final AccountService accountService;

    private final MarketDataClient marketDataClient;

    private final Map<String, PurchaseManager> activePurchases = new ConcurrentHashMap<>();

    // 필수적이지는 않다.
    private final AtomicBoolean isPurchasing = new AtomicBoolean(false);


    public void executeOrders() {

        log.info("시작");

        Flux.interval(Duration.ofMinutes(1))
                .filter(s -> !isPurchasing.get())
                .doOnNext(s -> log.info("가격체크중"))
                .flatMap(t -> marketDataClient.getAllMarketCodes())
                .filterWhen(this::shouldPurchase)
                .flatMap(this::initPurchase)
                .subscribe();

    }


    public void executeOrdersForTarget() {

        Map<String, Double> targetedCoins = new HashMap<>();

        Flux.interval(Duration.ofMinutes(5))
                .flatMap(s -> Flux.fromIterable(targetedCoins.entrySet()))
                .filterWhen(this::shouldPurchaseForTarget)
                .flatMap(entry -> initPurchase(entry.getKey()))
                .subscribe();
    }

    // 조건 체크 로직

    private Mono<Boolean> shouldPurchase(String market) {

        log.info("마켓코드 조건체크 시작  : {}", market);

        if (activePurchases.containsKey(market)) {
            return Mono.just(false);
        }

        /**
         * API 요청수제한 !! , 1초에 총 10번의 요청만이가능 -> 딜레이기능 사용.
         */
        return marketDataClient.getMarketData(market).delayElement(Duration.ofMillis(75)).map(
                //ex
                marketData -> {
                    double price = 1;
                    boolean b = marketData.getCurrentPrice() < price;
                    log.info("market : {} , condition : {}", market, b);
                    return b;
                }
        );

    }

    private Mono<Boolean> shouldPurchaseForTarget(Entry<String, Double> entry) {

        return marketDataClient.getMarketData(entry.getKey())
                .map(marketData -> marketData.getCurrentPrice() <= entry.getValue()
                        && !activePurchases.containsKey(entry.getKey()));

    }


    // 초기화
    private Mono<PurchaseManager> initPurchase(String market) {
        log.info("매수를위한 초기화 시작 : {}", market);
        return accountService.getAvailableBalance("KRW").flatMap(
                balance -> {
                    double amount = balance * 0.5;
                    log.info("매수시작 :{} , 할당된금액 : {}", market, balance);
                    PurchaseManager purchaseManager = PurchaseManager.create(market, amount);
                    activePurchases.put(market, purchaseManager);
                    isPurchasing.set(true);
                    return executePurchaseV2(purchaseManager).thenReturn(purchaseManager);
                }
        );
    }

    /**
     * 분할매수
     *
     * 최소금액 5000원을 executePurchase , getOrder 두번체크
     * 하지만 , API 중 주문가능금액을 호출할수있는 기능이있다 , 해당 기능을 이용하면 위와같이 번거로운 과정을 좀 생략할수있다. 
     */

    private Mono<String> executePurchase(PurchaseManager manager) {
        log.info("매수시작 : {}", manager.getMarket());

        return Flux.interval(Duration.ofMinutes(5))
                .take(3)
                .flatMap(s -> marketDataClient.getMarketData(manager.getMarket()))
                .filter(marketData -> executeAdditionalPurchase(manager, marketData))
                .flatMap(marketData -> {

                    double amount = manager.calculateRemainingAmount();

                    if (amount < 5000) {
                        log.info("매수금액 부족:{} , 필요한 최소금액: 5000, 현재금액 :{}", manager.getMarket(),
                                amount);
                        return Mono.just("매수금액부족");
                    }

                    log.info("매수 :{} , 매수금액 :{}", manager.getMarket(), amount);

                    return orderService.getOrderV2(manager.getMarket(), amount,
                                    null, "bid", "price")
                            .doOnNext(response -> {
                                manager.updatePurchaseManager(marketData.getCurrentPrice(), amount);
                                log.info("매수 : {}", manager.getMarket());

                                if (manager.getPurchaseCount() >= 3) {
                                    completePurchase(manager);
                                }

                            });
                })
                .collectList()
                .flatMap(
                        response -> {
                            completePurchase(manager);
                            return Mono.justOrEmpty(
                                    response.size() > 0 ? response.get(response.size() - 1)
                                            : "매수완료");
                        }
                );
    }

    /**
     * FLux.interval 기능은 즉시시작이아니기떄문에 , 내가 원하는시나리오랑 맞추려면 아래와같이 작동시켜야함 .
     */
    private Mono<String> executePurchaseV2(PurchaseManager manager) {

        log.info("매수시작 : {}", manager.getMarket());

        Mono<String> initialPurchase = getPurchase(manager);

        Flux<String> additionalPurchases = Flux.interval(Duration.ofMinutes(5)).take(2)
                .flatMap(s -> getPurchase(manager));

        return Flux.concat(initialPurchase, additionalPurchases)
                .collectList()
                .flatMap(
                        response -> {
                            completePurchase(manager);
                            return Mono.justOrEmpty(
                                    response.size() > 0 ? response.get(response.size() - 1)
                                            : "매수완료");
                        }
                );
    }

    private Mono<String> getPurchase(PurchaseManager manager) {

        return marketDataClient.getMarketData(manager.getMarket())
                .filter(marketData -> executeAdditionalPurchase(manager, marketData))
                .flatMap(marketData -> {

                    double amount = manager.calculateRemainingAmount() - 100;

                    log.info("매수 :{} , 매수금액 :{}", manager.getMarket(), amount);

                    return orderService.getOrderV2(manager.getMarket(), amount, null, "bid",
                                    "price")
                            .doOnNext(response -> {
                                manager.updatePurchaseManager(marketData.getCurrentPrice(), amount);
                                log.info("매수 : {}", manager.getMarket());

                                if (manager.getPurchaseCount() >= 3) {
                                    completePurchase(manager);
                                }

                            });

                }).thenReturn("매수완료");

    }

    private void completePurchase(PurchaseManager manager) {
        log.info("매수완료 : {} ", manager.getMarket());
//        activePurchases.remove(manager.getMarket());
        if (activePurchases.isEmpty()) {
            isPurchasing.set(false);
        }
    }


    private boolean executeAdditionalPurchase(PurchaseManager manager,
            CoinDataResponse marketData) {

        boolean result = manager.getPurchaseCount() == 0
                || marketData.getCurrentPrice() < manager.getLastPurchasePrice();

        log.info("추가매수 조건 체크 : {} , 결과 : {}", manager.getMarket(), result);

        return result;

    }


}
