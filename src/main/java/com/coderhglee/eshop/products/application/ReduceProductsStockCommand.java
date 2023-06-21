package com.coderhglee.eshop.products.application;

import java.util.List;
import com.coderhglee.eshop.core.SelfValidator;
import com.coderhglee.eshop.products.dto.ReduceProductDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReduceProductsStockCommand extends SelfValidator<ReduceProductStockCommand> {
    private final List<ReduceProductDto> products;
}
