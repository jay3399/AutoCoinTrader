package com.example.autocointrader.application.ui.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class TickerResponse {


    //현재가
    @JsonProperty("trade_price")
    private Double tradePrice;

    private Double openingPrice;
    private Double lowPrice;
    private Double highPrice;

    private String change;

    private Double changeRate;


    private Double tradeVolume;


    private Double accTradePrice;
    private Double accTradeVolume;




}
