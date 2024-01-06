package com.example.autocointrader.application.service;

import com.example.autocointrader.application.ui.response.AccountBalance;
import com.example.autocointrader.domain.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccountAppService {

    private final AccountService accountService;


    public Mono<Double> getBalance(String currency) {
        return getAccounts().filter(account -> account.getCurrency().equals(currency)).map(accountBalance -> accountBalance.getBalance() - accountBalance.getLocked()).next();
    }

    public Flux<String> getCurrencyOnAccounts() {

        return getAccounts().map(accountBalance -> accountBalance.getCurrency());

    }


    public Flux<AccountBalance> getAccounts() {

        return accountService.getAllAccounts();


    }



}
