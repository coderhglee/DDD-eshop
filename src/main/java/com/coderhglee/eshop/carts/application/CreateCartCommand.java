package com.coderhglee.eshop.carts.application;

import javax.validation.constraints.NotNull;
import com.coderhglee.eshop.core.SelfValidator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateCartCommand extends SelfValidator<CreateCartCommand> {
    @NotNull
    private final String customerId;
}
