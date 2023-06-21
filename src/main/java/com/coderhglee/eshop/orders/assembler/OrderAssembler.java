package com.coderhglee.eshop.orders.assembler;

import java.util.List;
import java.util.stream.Collectors;
import com.coderhglee.eshop.orders.domain.Order;
import com.coderhglee.eshop.orders.dto.OrderDto;
import com.coderhglee.eshop.orders.dto.OrderLineItemDto;

public class OrderAssembler {
    public static OrderDto toDto(Order order) {
        List<OrderLineItemDto> lineItems = order.getOrderLines().stream()
                .map(lineItem -> new OrderLineItemDto(lineItem.getProductId(),
                        lineItem.getProductName(), lineItem.getQuantity(),
                        lineItem.getOriginPriceToString()))
                .collect(Collectors.toUnmodifiableList());

        return new OrderDto(order.getStringId(), order.getCustomerId(), order.getAmountToString(),
                order.getShippingFeeToString(), order.getPaymentId(), lineItems);
    }
}
