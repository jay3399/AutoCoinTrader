package com.example.autocointrader.application.ui.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class OrderChanceResponse {


    // 매수 전용계좌의 balance 는 해당 코인 매수할수있는 원화 (KRW )
    // 매도 전용계좌는 , 해당 코인의 개수

    @JsonProperty("bid_account")
    private AccountInfo bidAccount;

    @JsonProperty("ask_account")
    private AccountInfo askAccount;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class AccountInfo {

        @JsonProperty("balance")
        private String balance;

        @JsonProperty("avg_buy_price")
        private String avgBuyPrice;


    }
}