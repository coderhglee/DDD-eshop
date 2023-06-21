package com.coderhglee.eshop.orders.application;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.coderhglee.eshop.orders.assembler.OrderAssembler;
import com.coderhglee.eshop.orders.domain.IOrderRepository;
import com.coderhglee.eshop.orders.domain.Order;
import com.coderhglee.eshop.orders.dto.OrderDto;
import com.coderhglee.eshop.orders.infrastructure.IPaymentService;
import com.coderhglee.eshop.products.application.ReduceProductsStockCommand;
import com.coderhglee.eshop.products.application.ReduceProductsStockUseCase;
import com.coderhglee.eshop.products.dto.ReduceProductDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompleteOrderService implements CompleteOrderUseCase {

    private final IOrderRepository orderRepository;

    private final IPaymentService paymentService;

    private final ReduceProductsStockUseCase reduceProductStockUseCase;


    @Transactional
    @Override
    public OrderDto execute(CompleteOrderCommand command) {
        command.validate();

        Order order = this.orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new NullPointerException("not found order."));

        if (!order.isOnwer(command.getCustomerId())) {
            throw new IllegalAccessError("주문에 권한이 없습니다.");
        }

        boolean completePayment = this.paymentService.checkCompletePayment(command.getPaymentId());

        if (!completePayment) {
            throw new IllegalStateException("결제가 완료되지 않았습니다.");
        }

        List<ReduceProductDto> reduceProducts = order.getOrderLines().stream().map(
                lineItem -> new ReduceProductDto(lineItem.getProductId(), lineItem.getQuantity()))
                .collect(Collectors.toUnmodifiableList());

        this.reduceProductStockUseCase.execute(new ReduceProductsStockCommand(reduceProducts));

        order.complete();

        return OrderAssembler.toDto(order);
    }

}
