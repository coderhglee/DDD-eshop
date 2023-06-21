package com.coderhglee.eshop.orders.application;

import static org.assertj.core.api.BDDAssertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import java.math.BigDecimal;
import javax.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.coderhglee.eshop.IntegrationTest;
import com.coderhglee.eshop.common.Money;
import com.coderhglee.eshop.orders.domain.IOrderRepository;
import com.coderhglee.eshop.orders.domain.Order;
import com.coderhglee.eshop.orders.domain.OrderLineItem;
import com.coderhglee.eshop.orders.domain.OrderStatus;
import com.coderhglee.eshop.orders.dto.OrderDto;
import com.coderhglee.eshop.orders.infrastructure.IPaymentService;
import com.coderhglee.eshop.products.application.ReduceProductsStockCommand;
import com.coderhglee.eshop.products.application.ReduceProductsStockUseCase;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.coderhglee.eshop.utils.TestUtils;
import net.jqwik.api.Arbitraries;

public class CompleteOrderUseCaseTest extends IntegrationTest {

    @Autowired
    CompleteOrderUseCase completeOrderUseCase;

    @MockBean
    IPaymentService paymentService;

    @MockBean
    ReduceProductsStockUseCase reduceProductsStockUseCase;

    @Autowired
    IOrderRepository orderRepository;

    @DisplayName("You can complete the generated spell.")
    @Transactional
    @Test
    void shouldOrderComplete() {
        // given
        FixtureMonkey sut = FixtureMonkey.builder()
                .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
                .register(Money.class,
                        fixture -> fixture.giveMeBuilder(Money.class).set("amount",
                                BigDecimal.valueOf(
                                        Arbitraries.integers().between(1000, 1000000).sample())))
                .register(OrderLineItem.class,
                        fixture -> fixture.giveMeBuilder(OrderLineItem.class)
                                .set("productId", TestUtils.generateUUIDToString())
                                .set("quantity", Arbitraries.integers().between(1, 100)))
                .build();

        Order order = orderRepository.save(sut.giveMeBuilder(Order.class)
                .set("customerId", TestUtils.generateUUIDToString())
                .set("paymentId", TestUtils.generateUUIDToString())
                .set("calculatedTotalAmount", Money.of("0")).set("shippingFee", Money.of("0"))
                .set("status", OrderStatus.DRAFT).size("orderLines", 5).sample());

        CompleteOrderCommand command = sut.giveMeBuilder(CompleteOrderCommand.class)
                .set("orderId", order.getStringId()).set("customerId", order.getCustomerId())
                .set("paymentId", order.getPaymentId()).sample();

        doReturn(Boolean.valueOf(true)).when(paymentService).checkCompletePayment(anyString());
        doNothing().when(reduceProductsStockUseCase).execute(any(ReduceProductsStockCommand.class));

        // when
        OrderDto completedOrder = completeOrderUseCase.execute(command);

        Order foundOrder = orderRepository.findById(completedOrder.getId()).get();

        // then
        then(foundOrder.getOrderLines()).hasSize(5);
        then(foundOrder.getStatus()).isEqualTo(OrderStatus.COMPLEATE);
    }
}
