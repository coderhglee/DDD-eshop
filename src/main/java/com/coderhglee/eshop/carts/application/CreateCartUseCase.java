package com.coderhglee.eshop.carts.application;

import com.coderhglee.eshop.carts.dto.CartDto;

public interface CreateCartUseCase {
    CartDto execute(CreateCartCommand command);
}
