package com.coderhglee.eshop.orders.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderSheetDto {
    private final String id;

    private final String calculatedTotalAmount;

    private final String shippingFee;

    private final String paymentAmount;

    private final List<OrderSheetLineItemDto> orderLineItemDtos;
}
