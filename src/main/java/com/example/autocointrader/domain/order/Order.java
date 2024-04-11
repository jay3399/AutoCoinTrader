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


    private Order(Side side, OrderType orderType, String market, Optional<Double> price, Optional<String> volume) {

        this.side = side;
        this.orderType = orderType;
        this.market = market;
        this.price = price;
        this.volume = volume;

    }

//    만약에 , 지정가까지 구현을한다면 두개타입이 넘어와서 아래와같이 구분을 해줘야하지만 , 당장은 필요가없다 .

//    public boolean isMarketOrder() {
//        return orderType == OrderType.MARKET;
//    }
//
//    public boolean isLimitOrder() {
//        return orderType == OrderType.LIMIT;
//    }

    public static Order createForMarketBid(String market, Optional<Double> price) {

        return new Order(Side.BID, OrderType.MARKET, market, price, null);

    }





}
