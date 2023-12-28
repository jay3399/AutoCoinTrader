package com.example.autocointrader.application.ui.controller;


import com.example.autocointrader.application.ui.response.OrderChanceResponse;
import com.example.autocointrader.application.ui.response.OrderChanceResponse.AccountInfo;
import com.example.autocointrader.domain.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping("/api/orders/create")
    public ResponseEntity<String> createOrder(@RequestParam String market, @RequestParam double price , @RequestParam String side) {

        Mono<String> order = orderService.getOrder(market, price, side);

        return ResponseEntity.ok(order.block());
    }


    @GetMapping("/api/balance/available")
    public ResponseEntity<String> getAvailableBalance(@RequestParam String currency) {

        Mono<Double> availableBalance = orderService.getAvailableBalance(currency);

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
        System.out.println("bidAccount. = " + bidAccount.getBalance());
        System.out.println("askAccount. = " + askAccount.getBalance());


        return ResponseEntity.ok(bidAccount.getBalance());
    }



}
