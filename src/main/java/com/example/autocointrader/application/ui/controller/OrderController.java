package com.example.autocointrader.application.ui.controller;


import com.example.autocointrader.application.service.AccountAppService;
import com.example.autocointrader.application.service.OrderAppService;
import com.example.autocointrader.application.service.OrderSchService;
import com.example.autocointrader.application.ui.response.OrderChanceResponse;
import com.example.autocointrader.application.ui.response.OrderChanceResponse.AccountInfo;
import com.example.autocointrader.domain.account.AccountService;
import com.example.autocointrader.domain.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private final AccountService accountService;

    private final OrderAppService orderAppService;

    private final AccountAppService accountAppService;

    private final OrderSchService orderSchService;


    @PostMapping("/api/orders/create")
    public ResponseEntity<String> createOrder(@RequestParam String market,
            @RequestParam String price, @RequestParam String side, @RequestParam String volume,
            @RequestParam String ordType) {

        Mono<String> order = orderService.getOrderV2(market, price, volume, side, ordType);

        return ResponseEntity.ok(order.block());
    }


    @PostMapping("/api/orders/sell/all")
    public ResponseEntity<String> createOrderForSellAllCoins(@RequestParam String market) {

        Mono<String> stringMono = orderAppService.sellAllCoins(market);
        return ResponseEntity.ok(stringMono.block());

    }


    @GetMapping("/api/balance/available")
    public ResponseEntity<String> getAvailableBalance(@RequestParam String currency) {

        Mono<Double> availableBalance = accountService.getAvailableBalance(currency);

        Double block = availableBalance.block();

        String response = "사용가능 잔고 " + block;

        System.out.println(response);

        return ResponseEntity.ok(response);


    }


    @GetMapping("/api/order/chance")
    public ResponseEntity<String> getOrderChance(@RequestParam String market) {

        Mono<OrderChanceResponse> orderChance = orderService.getOrderChance(market);
        OrderChanceResponse chanceResponse = orderChance.block();

        AccountInfo bidAccount = chanceResponse.getBidAccount();
        AccountInfo askAccount = chanceResponse.getAskAccount();

        return ResponseEntity.ok(bidAccount.getBalance());
    }

    @GetMapping("/api/order/test")
    public void test() {

        orderSchService.executeOrders();


    }


}
