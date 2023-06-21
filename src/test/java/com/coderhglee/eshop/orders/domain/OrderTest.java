package com.coderhglee.eshop.orders.domain;

import static org.assertj.core.api.BDDAssertions.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.coderhglee.eshop.common.Money;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.FixtureMonkeyBuilder;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import net.jqwik.api.Arbitraries;

public class OrderTest {

    FixtureMonkeyBuilder sut;

    @BeforeEach
    void before() {
        sut = FixtureMonkey.builder()
                .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
                .register(Money.class,
                        fixture -> fixture.giveMeBuilder(Money.class).set("amount",
                                BigDecimal.valueOf(
                                        Arbitraries.integers().between(1000, 1000000).sample())))
                .register(OrderLineItem.class, fixture -> fixture.giveMeBuilder(OrderLineItem.class)
                        .set("quantity", Arbitraries.integers().between(1, 100)));
    }

    @Test
    void shouldOrderCheckout() {
        // given
        Order order = sut.build().giveMeBuilder(Order.class).set("status", OrderStatus.DRAFT)
                .size("orderLines", 5).sample();

        // when
        order.checkout();

        // then
        then(order.getOrderLines()).hasSize(5);
        then(order.getCalculatedTotalAmount().isGraterThan("0")).isTrue();
        then(order.getShippingFee().isGraterThanEquals("0")).isTrue();
    }

    @DisplayName("An error occurs if the state of the order is complete.")
    @Test
    void whenOrderStatusCompleteThrowIllegalStateException() {
        // when
        Order order = sut.build().giveMeBuilder(Order.class).set("status", OrderStatus.COMPLEATE)
                .size("orderLines", 5).sample();

        // then
        thenThrownBy(() -> order.checkout()).isInstanceOf(IllegalStateException.class)
                .hasMessage("order status is not draft, cannot checkout");
    }

    @DisplayName("Order's shipping policy is applied.")
    @Test
    void whenCheckoutOrderShouldCalculateShippingFee() {
        // given
        sut.register(Money.class,
                fixture -> fixture.giveMeBuilder(Money.class).set("amount",
                        BigDecimal.valueOf(1000)))
                .register(OrderLineItem.class,
                        fixture -> fixture.giveMeBuilder(OrderLineItem.class).set("quantity", 5))
                .build();

        Order order = sut.build().giveMeBuilder(Order.class).set("status", OrderStatus.DRAFT)
                .size("orderLines", 5).sample();

        // when
        order.checkout();

        // then
        then(order.getAmountToString()).isEqualTo("25000");
        then(order.getShippingFee()).isEqualTo(Money.of("2500"));
    }

    @DisplayName("If the total price is less than 50,000, the shipping cost policy is applied to the order line item.")
    @Test
    void whenCheckoutOrderShouldCalculateShippingFeeLessThenAmount() {
        // given
        sut.register(Money.class,
                fixture -> fixture.giveMeBuilder(Money.class).set("amount",
                        BigDecimal.valueOf(10000)))
                .register(OrderLineItem.class,
                        fixture -> fixture.giveMeBuilder(OrderLineItem.class).set("quantity", 5))
                .build();

        Order order = sut.build().giveMeBuilder(Order.class).set("status", OrderStatus.DRAFT)
                .size("orderLines", 1).sample();

        // when
        order.checkout();

        // then
        then(order.getAmountToString()).isEqualTo("50000");
        then(order.getShippingFee()).isEqualTo(Money.of("2500"));
    }

    @DisplayName("AMOUNT is determined by Order's OrderLineItem OriginPrice.")
    @Test
    void whenCheckoutOrderShouldCalculateAmountFromLineItemOriginPriceSum() {
        // given
        sut.register(Money.class,
                fixture -> fixture.giveMeBuilder(Money.class).set("amount",
                        BigDecimal.valueOf(20000)))
                .register(OrderLineItem.class,
                        fixture -> fixture.giveMeBuilder(OrderLineItem.class).set("quantity", 2))
                .build();

        Order order = sut.build().giveMeBuilder(Order.class).set("status", OrderStatus.DRAFT)
                .size("orderLines", 5).sample();

        // when
        order.checkout();

        // then
        then(order.getCalculatedTotalAmount()).isEqualTo(Money.of("200000"));
        then(order.getShippingFee()).isEqualTo(Money.ZERO_MONEY);
    }
}
