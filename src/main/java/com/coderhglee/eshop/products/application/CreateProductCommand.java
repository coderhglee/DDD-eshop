package com.coderhglee.eshop.products.application;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import com.coderhglee.eshop.core.SelfValidator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateProductCommand extends SelfValidator<CreateProductCommand> {
    @NotNull
    private final String code;

    @NotNull
    private final String name;

    @Min(0)
    @NotNull
    private final String price;

    @Min(1)
    @NotNull
    private final int quantity;
}
