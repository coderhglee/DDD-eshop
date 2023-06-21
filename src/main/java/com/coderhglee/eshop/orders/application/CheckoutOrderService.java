package com.coderhglee.eshop.orders.application;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.coderhglee.eshop.carts.application.GetCartUseCase;
import com.coderhglee.eshop.carts.dto.CartDto;
import com.coderhglee.eshop.orders.assembler.OrderAssembler;
import com.coderhglee.eshop.orders.domain.IOrderRepository;
import com.coderhglee.eshop.orders.domain.Order;
import com.coderhglee.eshop.orders.domain.OrderLineItem;
import com.coderhglee.eshop.orders.dto.OrderDto;
import com.coderhglee.eshop.orders.infrastructure.IPaymentService;
import com.coderhglee.eshop.products.application.GetProductUseCase;
import com.coderhglee.eshop.products.dto.ProductDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CheckoutOrderService implements CheckoutOrderUseCase {

    private final GetCartUseCase getCartUseCase;

    private final GetProductUseCase getProductUseCase;

    private final IOrderRepository orderRepository;

    private final IPaymentService paymentService;

    @Transactional
    @Override
    public OrderDto execute(CheckoutOrderCommand command) {
        command.validate();

        CartDto cart = getCartUseCase.findById(command.getCartId());

        if (!cart.getCustomerId().equals(command.getCustomerId())) {
            throw new IllegalAccessError("No authority in the shopping cart.");
        }

        Order draftOrder = Order.createDraftOrder(command.getCustomerId());

        orderRepository.save(draftOrder);

        List<OrderLineItem> lineItems = cart.getCartLineItemDtos().stream().map((dto) -> {
            ProductDto productDto = this.getProductUseCase.findById(dto.getProductId());

            return OrderLineItem.of(productDto.getId(), productDto.getName(), dto.getQuantity(),
                    productDto.getPrice());
        }).collect(Collectors.toUnmodifiableList());

        draftOrder.addAllLineItems(lineItems);

        draftOrder.checkout();

        String id = this.paymentService.requsetPayment(draftOrder.getStringId(),
                draftOrder.getCustomerId(), draftOrder.getPaymentAmountToString());

        draftOrder.requsetPayment(id);

        return OrderAssembler.toDto(draftOrder);
    }

}
