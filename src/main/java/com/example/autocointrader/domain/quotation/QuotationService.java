package com.example.autocointrader.domain.quotation;

import com.example.autocointrader.application.ui.response.CoinDataResponse;
import com.example.autocointrader.infrastructure.api.quotation.UpbitQuotationClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class QuotationService {

    private final UpbitQuotationClient upbitQuotationClient;


    public Mono<Double> getCurrentPrice(String marketCode) {

        return upbitQuotationClient.getMarketData(marketCode).map(CoinDataResponse::getCurrentPrice);

    }

    public Mono<Double> getVolume(String marketCode) {

        return upbitQuotationClient.getMarketData(marketCode).map(CoinDataResponse::getVolume);
    }


}
