package com.coderhglee.eshop.products.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProductDto {
    @NotNull
    private final String id;
    @NotNull
    private final String code;
    @NotNull
    private final String name;
    @NotNull
    private final String price;
    @NotNull
    private final int quantity;
}
