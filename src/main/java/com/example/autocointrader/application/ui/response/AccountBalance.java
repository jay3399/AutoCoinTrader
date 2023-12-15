package com.example.autocointrader.application.ui.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class AccountBalance {

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("balance")
    private double balance;

    @JsonProperty("locked")
    private double locked;


}
