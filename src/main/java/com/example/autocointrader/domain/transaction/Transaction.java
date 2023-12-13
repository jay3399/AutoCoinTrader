package com.example.autocointrader.domain.transaction;


import com.example.autocointrader.domain.order.Order;

public class Transaction {

    private Long id;

    //총 가격
    private int amount;

    //평균가격 ( 분할매수 )
    private int price;

    private Order order;


}

