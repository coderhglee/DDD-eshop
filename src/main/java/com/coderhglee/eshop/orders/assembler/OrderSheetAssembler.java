package com.coderhglee.eshop.orders.assembler;

import java.util.List;
import java.util.stream.Collectors;
import com.coderhglee.eshop.orders.domain.Order;
import com.coderhglee.eshop.orders.dto.OrderSheetDto;
import com.coderhglee.eshop.orders.dto.OrderSheetLineItemDto;

public class OrderSheetAssembler {
    public static OrderSheetDto toDto(Order order) {
        String moneyFormat = "#,###원";
        String quantityFormat = "%d개";
        List<OrderSheetLineItemDto> lineItems = order.getOrderLines().stream()
                .map(lineItem -> new OrderSheetLineItemDto(lineItem.getProductName(),
                        String.format(quantityFormat, lineItem.getQuantity())))
                .collect(Collectors.toUnmodifiableList());

        return new OrderSheetDto(order.getStringId(),
                order.getCalculatedTotalAmount().getAmountToFormatString(moneyFormat),
                order.getShippingFee().getAmountToFormatString(moneyFormat),
                order.getPaymentAmount().getAmountToFormatString(moneyFormat), lineItems);
    }
}
