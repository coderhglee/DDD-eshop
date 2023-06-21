package com.coderhglee.eshop.carts.application;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import com.coderhglee.eshop.core.SelfValidator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AddCartLineItemCommand extends SelfValidator<AddCartLineItemCommand> {
    @NotNull
    private final String customerId;

    @NotNull
    private final String cartId;

    @NotNull
    private final String productCode;

    @Min(1)
    @NotNull
    private final int quantity;
}
