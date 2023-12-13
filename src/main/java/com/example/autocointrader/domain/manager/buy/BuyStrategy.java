package com.example.autocointrader.domain.manager.buy;

import com.example.autocointrader.application.ui.response.CoinCurrentPrice;

public interface BuyStrategy {

    boolean shouldBuy(CoinCurrentPrice coinCurrentPrice);


}
