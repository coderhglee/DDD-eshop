package com.coderhglee.eshop.orders.application;

import com.coderhglee.eshop.orders.dto.OrderDto;

public interface CheckoutOrderUseCase {
    OrderDto execute(CheckoutOrderCommand command);
}
