package com.example.autocointrader.domain.manager.buy;

import com.example.autocointrader.application.ui.response.CoinCurrentPrice;

public class LowPriceBuyStrategy implements BuyStrategy{

    private final double price;

    public LowPriceBuyStrategy(double price) {
        this.price = price;
    }
    @Override
    public boolean shouldBuy(CoinCurrentPrice coinCurrentPrice) {

        return coinCurrentPrice.getCurrentPrice() < price;

    }
}
