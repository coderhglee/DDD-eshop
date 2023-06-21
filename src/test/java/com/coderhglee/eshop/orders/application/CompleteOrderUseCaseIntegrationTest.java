package com.coderhglee.eshop.orders.application;

import static org.assertj.core.api.BDDAssertions.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;
import com.coderhglee.eshop.common.Money;
import com.coderhglee.eshop.orders.domain.IOrderRepository;
import com.coderhglee.eshop.orders.domain.Order;
import com.coderhglee.eshop.orders.domain.OrderLineItem;
import com.coderhglee.eshop.orders.domain.OrderStatus;
import com.coderhglee.eshop.products.domain.Product;
import com.coderhglee.eshop.products.domain.repository.IProductRepository;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.coderhglee.eshop.utils.TestUtils;
import net.jqwik.api.Arbitraries;

@ActiveProfiles("test")
@SpringBootTest
public class CompleteOrderUseCaseIntegrationTest {

    @Autowired
    CompleteOrderUseCase completeOrderUseCase;

    @Autowired
    IOrderRepository orderRepository;

    @Autowired
    IProductRepository productRepository;

    @Autowired
    TransactionTemplate template;

    @Test()
    public void whenMultiCustomersDecreaseProductGratherQuantityThrowSoldOutExceptionTest()
            throws InterruptedException {
        FixtureMonkey sut = FixtureMonkey.builder()
                .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
                .register(Money.class,
                        fixture -> fixture.giveMeBuilder(Money.class).set("amount",
                                BigDecimal.valueOf(
                                        Arbitraries.integers()
                                                .between(1000, 1000000)
                                                .sample())))
                .register(OrderLineItem.class,
                        fixture -> fixture.giveMeBuilder(OrderLineItem.class)
                                .set("productId", TestUtils.generateUUIDToString())
                                .set("quantity", Arbitraries.integers().between(1,
                                        100)))
                .build();

        // given
        Product p1 = productRepository.save(sut.giveMeBuilder(Product.class).set("name", "Some Product")
                .set("code", Arbitraries.strings().numeric().ofLength(6)).set("quantity", 10)
                .sample());

        Product p2 = productRepository.save(sut.giveMeBuilder(Product.class).set("name", "Some Product")
                .set("code", Arbitraries.strings().numeric().ofLength(6)).set("quantity", 10)
                .sample());

        Order order1 = orderRepository.save(sut.giveMeBuilder(Order.class)
                .set("customerId", TestUtils.generateUUIDToString())
                .set("paymentId", TestUtils.generateUUIDToString())
                .set("calculatedTotalAmount", Money.of("0")).set("shippingFee", Money.of("0"))
                .set("orderLines", Arrays.asList(
                        sut.giveMeBuilder(OrderLineItem.class)
                                .set("productId", p1.getStringId())
                                .set("quantity", 5).sample(),
                        sut.giveMeBuilder(OrderLineItem.class)
                                .set("productId", p2.getStringId())
                                .set("quantity", 5).sample()))
                .set("status", OrderStatus.DRAFT).sample());

        Order order2 = orderRepository.save(sut.giveMeBuilder(Order.class)
                .set("customerId", TestUtils.generateUUIDToString())
                .set("paymentId", TestUtils.generateUUIDToString())
                .set("calculatedTotalAmount", Money.of("0")).set("shippingFee", Money.of("0"))
                .set("orderLines", Arrays.asList(
                        sut.giveMeBuilder(OrderLineItem.class)
                                .set("productId", p2.getStringId())
                                .set("quantity", 5).sample(),
                        sut.giveMeBuilder(OrderLineItem.class)
                                .set("productId", p1.getStringId())
                                .set("quantity", 5).sample()))
                .set("status", OrderStatus.DRAFT).sample());

        this.productRepository.save(p1);
        this.productRepository.save(p2);
        Order savedOrder1 = this.orderRepository.save(order1);
        Order savedOrder2 = this.orderRepository.save(order2);

        // when
        int numberOfThreads = 3;

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Runnable> runnables = new ArrayList<>();
        List<Exception> exceptions = new ArrayList<>();

        Runnable customer = () -> {
            try {
                completeOrderUseCase.execute(new CompleteOrderCommand(savedOrder1.getStringId(),
                        savedOrder1.getCustomerId(), savedOrder1.getPaymentId()));
            } catch (Exception exception) {
                exceptions.add(exception);
            }
        };

        Runnable customer2 = () -> {
            try {
                completeOrderUseCase.execute(new CompleteOrderCommand(savedOrder2.getStringId(),
                        savedOrder2.getCustomerId(), savedOrder2.getPaymentId()));
            } catch (Exception exception) {
                exceptions.add(exception);
            }
        };

        runnables.add(customer);
        executorService.submit(customer);
        runnables.add(customer2);
        executorService.submit(customer2);

        executorService.shutdown();
        executorService.awaitTermination(2, TimeUnit.SECONDS);

        // then
        then(exceptions).hasSize(0);
    }
}
