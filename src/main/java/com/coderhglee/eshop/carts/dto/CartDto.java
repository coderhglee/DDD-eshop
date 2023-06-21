package com.coderhglee.eshop.carts.dto;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CartDto {
    @NotNull
    private final String id;

    @NotNull
    private final String customerId;

    @NotNull
    private final List<CartLineItemDto> cartLineItemDtos;
}
