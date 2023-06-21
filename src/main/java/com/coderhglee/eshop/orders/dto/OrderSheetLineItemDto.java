package com.coderhglee.eshop.orders.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderSheetLineItemDto {
    private final String productName;

    private final String quantity;
}
