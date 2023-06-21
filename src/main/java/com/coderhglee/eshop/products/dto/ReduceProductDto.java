package com.coderhglee.eshop.products.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReduceProductDto {
    private final String productId;
    private final int quantity;
}
