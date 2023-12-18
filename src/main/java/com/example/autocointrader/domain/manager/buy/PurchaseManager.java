package com.example.autocointrader.domain.manager.buy;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseManager {

    private String market;
    private double totalAmount;
    private int purchaseCount;
    private double lastPurchasePrice;
    private LocalDateTime lastCheckedTime;




}
