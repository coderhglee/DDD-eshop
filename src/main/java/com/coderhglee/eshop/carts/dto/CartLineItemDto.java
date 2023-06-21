package com.coderhglee.eshop.carts.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CartLineItemDto {
    @NotNull
    private final String productId;

    @NotNull
    private final int quantity;
}
