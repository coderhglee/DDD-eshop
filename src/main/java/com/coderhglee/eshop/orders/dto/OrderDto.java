package com.coderhglee.eshop.orders.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderDto {
    private final String id;

    private final String customerId;

    private final String amount;

    private final String shippingFee;

    private final String paymentId;

    private final List<OrderLineItemDto> orderLineItemDtos;
}
