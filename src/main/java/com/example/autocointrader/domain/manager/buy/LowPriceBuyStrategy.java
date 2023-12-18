package com.example.autocointrader.domain.manager.buy;

import com.example.autocointrader.application.ui.response.CoinDataResponse;

public class LowPriceBuyStrategy implements BuyStrategy{

    private final double price;

    public LowPriceBuyStrategy(double price) {
        this.price = price;
    }

    @Override
    public boolean shouldBuy(CoinDataResponse coinDataResponse) {
        return coinDataResponse.getCurrentPrice() < price;
    }
}
