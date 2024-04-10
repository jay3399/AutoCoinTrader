package com.example.autocointrader.domain.order;

import java.util.Optional;
import lombok.Getter;

@Getter
public class Order {

    private String market;
    private Side side;
    private OrderType orderType;

    private Optional<Double> price;
    private Optional<String> volume;




}
