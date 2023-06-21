package com.coderhglee.eshop.orders.application;

import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.coderhglee.eshop.orders.assembler.OrderSheetAssembler;
import com.coderhglee.eshop.orders.domain.IOrderRepository;
import com.coderhglee.eshop.orders.domain.Order;
import com.coderhglee.eshop.orders.dto.OrderSheetDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FindCompleteOrderService implements FindCompleteOrderUseCase {

    private final IOrderRepository orderRepository;

    @Transactional(readOnly = true)
    @Override
    public OrderSheetDto execute(FindCompleteOrderQuery query) {
        Order order = this.orderRepository.findById(query.getOrderId())
                .orElseThrow(() -> new NoSuchElementException("not found order."));

        if (!order.isComplete()) {
            throw new NoSuchElementException("There is no order completed.");
        }

        if (!order.isOnwer(query.getCustomerId())) {
            throw new IllegalAccessError("There is no authority in the order.");
        }

        return OrderSheetAssembler.toDto(order);
    }

}
