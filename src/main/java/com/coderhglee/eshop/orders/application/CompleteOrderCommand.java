package com.coderhglee.eshop.orders.application;

import javax.validation.constraints.NotNull;
import com.coderhglee.eshop.core.SelfValidator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CompleteOrderCommand extends SelfValidator<CompleteOrderCommand> {
    @NotNull
    private final String orderId;

    @NotNull
    private final String customerId;

    @NotNull
    private final String paymentId;
}
