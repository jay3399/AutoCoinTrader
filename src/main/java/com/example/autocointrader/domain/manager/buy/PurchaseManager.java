package com.example.autocointrader.domain.manager.buy;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseManager {

    private String market;
    private double totalAmount;
    private double usedAmount;
    private int purchaseCount;
    private double lastPurchasePrice;
    private LocalDateTime lastCheckedTime;

    private PurchaseManager(String market, double totalAmount) {
        this.market = market;
        this.totalAmount = totalAmount;
        this.usedAmount = 0;
        this.purchaseCount = 0;
        this.lastPurchasePrice = 0;
        this.lastCheckedTime = LocalDateTime.now();
    }

    public double calculateRemainingAmount() {
        return this.totalAmount - this.usedAmount / 2;
    }

    public void updatePurchaseManager(double lastPurchasePrice, double purchaseAmount) {

        this.lastPurchasePrice = lastPurchasePrice;
        this.usedAmount += purchaseAmount;
        this.purchaseCount++;


    }


    public static PurchaseManager create(String market, double totalAmount) {

        return new PurchaseManager(market, totalAmount);

    }


}
