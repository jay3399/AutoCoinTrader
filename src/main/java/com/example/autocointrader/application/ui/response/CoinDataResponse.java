package com.example.autocointrader.application.ui.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class CoinDataResponse {


    @JsonProperty("market")
    private String symbol;
    @JsonProperty("trade_price")
    private double currentPrice;
    @JsonProperty("prev_closing_price")
    private double prevClosingPrice;

    @JsonProperty("trade_volume")
    private double volume;
    @JsonProperty("acc_trade_volume_24h")
    private double volumeForDay;


}
