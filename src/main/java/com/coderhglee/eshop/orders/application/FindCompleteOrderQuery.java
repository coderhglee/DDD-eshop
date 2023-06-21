package com.coderhglee.eshop.orders.application;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FindCompleteOrderQuery {
    private final String orderId;

    private final String customerId;
}
