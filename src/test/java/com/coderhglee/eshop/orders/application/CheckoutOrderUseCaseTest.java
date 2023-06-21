package com.coderhglee.eshop.orders.application;

import static org.assertj.core.api.BDDAssertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import javax.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.coderhglee.eshop.carts.application.GetCartUseCase;
import com.coderhglee.eshop.carts.dto.CartDto;
import com.coderhglee.eshop.carts.dto.CartLineItemDto;
import com.coderhglee.eshop.common.Money;
import com.coderhglee.eshop.orders.domain.IOrderRepository;
import com.coderhglee.eshop.orders.domain.Order;
import com.coderhglee.eshop.orders.dto.OrderDto;
import com.coderhglee.eshop.orders.infrastructure.IPaymentService;
import com.coderhglee.eshop.products.application.GetProductUseCase;
import com.coderhglee.eshop.products.dto.ProductDto;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.coderhglee.eshop.IntegrationTest;
import com.coderhglee.eshop.utils.TestUtils;
import net.jqwik.api.Arbitraries;

@SpringBootTest
public class CheckoutOrderUseCaseTest extends IntegrationTest {

    @Autowired
    CheckoutOrderUseCase checkoutOrderUseCase;

    @Autowired
    IOrderRepository orderRepository;

    @MockBean
    GetCartUseCase getCartUseCase;

    @MockBean
    GetProductUseCase getProductUseCase;

    @MockBean
    IPaymentService paymentService;

    @Transactional
    @DisplayName("You can create an order with the item in the cart.")
    @Test
    void shouldCheckoutOrderUsingCart() {
        // given
        FixtureMonkey sut = FixtureMonkey.builder().register(Money.class,
                fixture -> fixture.giveMeBuilder(Money.class).set("amount",
                        BigDecimal.valueOf(Arbitraries.integers().between(1000, 1000000).sample())))
                .register(CartLineItemDto.class,
                        fixture -> fixture.giveMeBuilder(CartLineItemDto.class)
                                .set("productId", TestUtils.generateUUIDToString())
                                .set("quantity", Arbitraries.integers().between(1, 100)))
                .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE).build();

        CartDto cart = sut.giveMeBuilder(CartDto.class).set("customerId", TestUtils.generateUUIDToString())
                .size("cartLineItemDtos", 5).build().sample();

        CheckoutOrderCommand command = sut.giveMeBuilder(CheckoutOrderCommand.class).set("cartId", cart.getId())
                .set("customerId", cart.getCustomerId()).build().sample();

        when(getCartUseCase.findById(command.getCartId())).thenReturn(cart);
        when(getProductUseCase.findById(anyString())).thenAnswer((answer) -> {
            return sut.giveMeBuilder(ProductDto.class)
                    .set("price", Arbitraries.integers().between(1000, 1000000).sample().toString())
                    .set("quantity", Arbitraries.integers().between(1, 100).sample()).sample();
        });

        String paymentId = TestUtils.generateUUIDToString();
        doReturn(paymentId).when(paymentService).requsetPayment(anyString(), anyString(),
                anyString());

        // when
        OrderDto draftOrder = checkoutOrderUseCase.execute(command);

        Order foundOrder = orderRepository.findById(draftOrder.getId()).get();
        // then
        then(foundOrder).isNotNull();
        then(foundOrder.getOrderLines()).hasSize(cart.getCartLineItemDtos().size());
        then(foundOrder.getPaymentId()).isEqualTo(paymentId);
    }

    @DisplayName("If the Cart Owner is different, IllegalaccessException occurs.")
    @Test
    void whenCartOwnerNotEqualsThrowIllegalAccessException() {
        // given
        FixtureMonkey sut = FixtureMonkey.builder()
                .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE).build();

        CartDto cart = sut.giveMeBuilder(CartDto.class)
                .set("customerId", TestUtils.generateUUIDToString()).build().sample();

        CheckoutOrderCommand command = sut.giveMeBuilder(CheckoutOrderCommand.class).build().sample();

        doReturn(cart).when(getCartUseCase).findById(command.getCartId());

        // when then
        thenThrownBy(() -> checkoutOrderUseCase.execute(command))
                .isInstanceOf(IllegalAccessError.class).hasMessage("No authority in the shopping cart.");
    }
}
