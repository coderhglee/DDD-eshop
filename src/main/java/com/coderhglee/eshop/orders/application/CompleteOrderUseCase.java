package com.coderhglee.eshop.orders.application;

import com.coderhglee.eshop.orders.dto.OrderDto;

public interface CompleteOrderUseCase {
    OrderDto execute(CompleteOrderCommand command);
}
