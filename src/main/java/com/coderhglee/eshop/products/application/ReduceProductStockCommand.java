package com.coderhglee.eshop.products.application;

import com.coderhglee.eshop.core.SelfValidator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReduceProductStockCommand extends SelfValidator<ReduceProductStockCommand> {
    private final String productId;
    private final int quantity;
}
