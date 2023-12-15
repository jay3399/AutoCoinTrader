package com.example.autocointrader.domain.manager.buy;

import com.example.autocointrader.application.ui.response.CoinCurrentPrice;
import com.example.autocointrader.application.ui.response.CoinDataResponse;

public interface BuyStrategy {

    boolean shouldBuy(CoinDataResponse coinDataResponse);


}
