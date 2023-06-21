package com.coderhglee.eshop.orders.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderLineItemDto {
    private final String productId;

    private final String productName;

    private final int quantity;

    private final String originPrice;
}
