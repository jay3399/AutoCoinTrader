package com.example.autocointrader.application.ui.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class OrderChanceResponse {


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