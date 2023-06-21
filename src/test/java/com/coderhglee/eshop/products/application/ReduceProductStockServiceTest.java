package com.coderhglee.eshop.products.application;

import static org.assertj.core.api.BDDAssertions.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import com.coderhglee.eshop.common.Money;
import com.coderhglee.eshop.products.domain.Product;
import com.coderhglee.eshop.products.domain.repository.IProductRepository;
import com.coderhglee.eshop.products.exception.SoldOutException;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.coderhglee.eshop.IntegrationTest;
import net.jqwik.api.Arbitraries;

public class ReduceProductStockServiceTest extends IntegrationTest {

    @Autowired
    ReduceProductStockUseCase reduceProductStockUseCase;

    @Autowired
    TransactionTemplate template;

    @Autowired
    IProductRepository productRepository;

    FixtureMonkey sut = FixtureMonkey.builder().register(Money.class,
            fixture -> fixture.giveMeBuilder(Money.class).set("amount",
                    BigDecimal.valueOf(Arbitraries.integers().between(1000, 1000000).sample())))
            .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE).build();

    @Test
    void shouldDecreaseProductQuantity() {
        // given
        Product existProduct = productRepository.save(sut.giveMeBuilder(Product.class)
                .set("name", "Some Product").set("code", Arbitraries.strings().numeric().ofLength(6))
                .set("quantity", 10).sample());

        // when
        reduceProductStockUseCase
                .execute(new ReduceProductStockCommand(existProduct.getStringId(), 10));

        // then
        then(productRepository.findById(existProduct.getStringId()).get().getQuantity()).isZero();

        thenThrownBy(() -> reduceProductStockUseCase
                .execute(new ReduceProductStockCommand(existProduct.getStringId(), 10)))
                .isInstanceOf(SoldOutException.class);
    }

    @Test
    void whenSoldOutProductThrowSoldOutException() {
        // given
        Product existProduct = productRepository.save(sut.giveMeBuilder(Product.class)
                .set("name", "Some Product").set("code", Arbitraries.strings().numeric().ofLength(6))
                .set("quantity", 10).sample());

        // when then
        thenThrownBy(() -> reduceProductStockUseCase
                .execute(new ReduceProductStockCommand(existProduct.getStringId(), 11)))
                .isInstanceOf(SoldOutException.class);
    }

    @Test()
    public void whenMultiCustomersDecreaseProductGratherQuantityThrowSoldOutException()
            throws InterruptedException {
        // given
        Product existProduct = productRepository.save(sut.giveMeBuilder(Product.class)
                .set("name", "Some Product").set("code", Arbitraries.strings().numeric().ofLength(6))
                .set("quantity", 10).sample());

        // when
        int numberOfThreads = 3;

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Runnable> runnables = new ArrayList<>();
        List<Exception> exceptions = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            Runnable customer = () -> {
                try {
                    reduceProductStockUseCase
                            .execute(new ReduceProductStockCommand(existProduct.getStringId(), 5));
                } catch (SoldOutException soldOutException) {
                    exceptions.add(soldOutException);
                }
            };

            runnables.add(customer);
            executorService.submit(customer);
        }

        executorService.shutdown();
        executorService.awaitTermination(2, TimeUnit.SECONDS);

        // then
        then(exceptions).hasSizeGreaterThan(0);
    }

    @Test()
    public void whenMultiCustomersDecreaseProductGratherQuantityThrowSoldOutExceptionTest()
            throws InterruptedException {
        // given
        Product p1 = productRepository.save(sut.giveMeBuilder(Product.class).set("name", "Some Product")
                .set("code", Arbitraries.strings().numeric().ofLength(6)).set("quantity", 10)
                .sample());

        Product p2 = productRepository.save(sut.giveMeBuilder(Product.class).set("name", "Some Product")
                .set("code", Arbitraries.strings().numeric().ofLength(6)).set("quantity", 10)
                .sample());

        // when
        int numberOfThreads = 3;

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Runnable> runnables = new ArrayList<>();
        List<Exception> exceptions = new ArrayList<>();

        Runnable customer = () -> {
            template.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    try {
                        reduceProductStockUseCase
                                .execute(new ReduceProductStockCommand(p1.getStringId(), 5));
                        reduceProductStockUseCase
                                .execute(new ReduceProductStockCommand(p2.getStringId(), 5));
                    } catch (SoldOutException soldOutException) {
                        exceptions.add(soldOutException);
                    }
                }
            });
        };

        Runnable customer2 = () -> {
            template.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    try {
                        reduceProductStockUseCase
                                .execute(new ReduceProductStockCommand(p2.getStringId(), 5));
                        reduceProductStockUseCase
                                .execute(new ReduceProductStockCommand(p1.getStringId(), 5));
                    } catch (SoldOutException soldOutException) {
                        exceptions.add(soldOutException);
                    }
                }
            });
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
