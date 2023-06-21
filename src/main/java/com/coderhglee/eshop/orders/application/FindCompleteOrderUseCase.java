package com.coderhglee.eshop.orders.application;

import com.coderhglee.eshop.orders.dto.OrderSheetDto;

public interface FindCompleteOrderUseCase {
    OrderSheetDto execute(FindCompleteOrderQuery query);
}
