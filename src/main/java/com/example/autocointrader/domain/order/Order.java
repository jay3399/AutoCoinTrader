package com.example.autocointrader.domain.order;

import java.util.Optional;
import lombok.Getter;

@Getter
public class Order {

    private String market;
    private Side side;
    private OrderType orderType;

    /**
     * 시장가 매수시 , 총 매수금액
     * 지정가 매수시 , 매수가격
     *
     * 시장가 매도시 , 총 매도금액
     * 지정가 매도시 , 매도가격
     */
    private Optional<Double> price;

    /**
     * 시장가 매수시 , 값 필요없음
     * 지정가 매수시 , 총 개수
     *
     * 시장가 매도시 , 값 필요없음
     * 지정가 매도시 , 총개수
     */
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

    public boolean checkBalance(double balance) {

        if (side == Side.BID) {
            price.orElseThrow(() -> new IllegalArgumentException("매수금액 필요"));
            return balance > price.get();
        }

    }





}
