package com.coderhglee.eshop.products.application;

import static org.assertj.core.api.BDDAssertions.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.coderhglee.eshop.common.Money;
import com.coderhglee.eshop.products.domain.Product;
import com.coderhglee.eshop.products.domain.repository.IProductRepository;
import com.coderhglee.eshop.products.exception.SoldOutException;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.coderhglee.eshop.IntegrationTest;
import com.coderhglee.eshop.utils.TestUtils;
import net.jqwik.api.Arbitraries;

public class ProductStockAvailabilityServiceTest extends IntegrationTest {

    @Autowired
    ProductStockAvailabilityUseCase productStockAvailabilityUseCase;

    @Autowired
    IProductRepository productRepository;

    FixtureMonkey sut = FixtureMonkey.builder().register(Money.class,
            fixture -> fixture.giveMeBuilder(Money.class).set("amount",
                    BigDecimal.valueOf(Arbitraries.integers().between(1000, 1000000).sample())))
            .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE).build();

    @Nested
    @DisplayName("If the inventory of the product exists as much as the quantity")
    class is_product_stock_available {
        Product existProduct;

        @BeforeEach
        void before() {
            Product product = sut.giveMeBuilder(Product.class).set("name", "Some Product")
                    .set("code", Arbitraries.strings().numeric().ofLength(6)).set("quantity", 10)
                    .sample();

            existProduct = productRepository.save(product);
        }

        @DisplayName("Returns the product that can be purchased.")
        @Test
        void shouldPurchaseProduct() {
            then(productStockAvailabilityUseCase
                    .checkAvailableForPurchase(existProduct.getStringId(), 9)).isNotNull();
        }
    }

    @Nested
    @DisplayName("If the inventory of the product does not exist as much as the quantity")
    class is_product_stock_not_available {
        Product existProduct;

        @BeforeEach
        void before() {
            Product product = sut.giveMeBuilder(Product.class).set("name", "Some Product")
                    .set("code", Arbitraries.strings().numeric().ofLength(6)).set("quantity", 1)
                    .sample();

            existProduct = productRepository.save(product);
        }

        @DisplayName("SoldOutException occurs.")
        @Test
        void shouldPurchaseProduct() {
            thenThrownBy(() -> productStockAvailabilityUseCase
                    .checkAvailableForPurchase(existProduct.getStringId(), 3))
                    .isInstanceOf(SoldOutException.class);
        }
    }

    @DisplayName("If there is no product of the same product ID, the NullPointerException occurs.")
    @Test
    void whenProductNotExistThrowNullPointException() {
        thenThrownBy(() -> productStockAvailabilityUseCase
                .checkAvailableForPurchase(TestUtils.generateUUIDToString(), 3))
                .isInstanceOf(NullPointerException.class)
                .hasMessageMatching("The product cannot be found.");
    }

    @DisplayName("If the product ID is NULL, IllegalargumentalException occurs.")
    @Test
    void whenIdNullThrowIllegalArgumentException() {
        thenThrownBy(() -> productStockAvailabilityUseCase.checkAvailableForPurchase(null, 3))
                .isInstanceOf(IllegalArgumentException.class).hasMessageMatching("The request is not correct.");
    }

    @DisplayName("If the quantity is smaller than zero, Illegalargumexception occurs.")
    @Test
    void whenQuantityIsZeroThrowIllegalArgumentException() {
        thenThrownBy(() -> productStockAvailabilityUseCase.checkAvailableForPurchase(null, -1))
                .isInstanceOf(IllegalArgumentException.class).hasMessageMatching("The request is not correct.");
    }
}
