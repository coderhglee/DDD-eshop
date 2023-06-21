package com.coderhglee.eshop.products.application;

import static org.assertj.core.api.BDDAssertions.*;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import com.coderhglee.eshop.common.Money;
import com.coderhglee.eshop.products.domain.Product;
import com.coderhglee.eshop.products.domain.repository.IProductRepository;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.coderhglee.eshop.IntegrationTest;
import net.jqwik.api.Arbitraries;

public class CreateProductServiceTest extends IntegrationTest {

    @Autowired
    CreateProductUseCase createProductUseCase;

    @Autowired
    IProductRepository productRepository;

    FixtureMonkey sut = FixtureMonkey.builder()
            .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
            .register(Money.class,
                    fixture -> fixture.giveMeBuilder(Money.class).set("amount", Money
                            .of(Arbitraries.integers().between(1000, 1000000).sample()
                                    .toString())))
            .build();

    @DisplayName("If you enter the correct information in the product,")
    @Nested
    class create_product {

        CreateProductCommand command;

        @BeforeEach
        void before() {
            command = sut.giveMeBuilder(CreateProductCommand.class)
                    .set("code", Arbitraries.strings().numeric().ofLength(6))
                    .set("price", Arbitraries.integers().between(1000, 1000000).sample().toString())
                    .set("name", "tumbler").set("quantity", Arbitraries.integers().between(1, 100))
                    .sample();
        }

        @DisplayName("You can create a product.")
        @Test
        void should_create_product() {
            Product createdProduct = createProductUseCase.execute(command);

            then(createdProduct.getStringId()).isNotNull();
            then(createdProduct.getCode()).isNotNull();
            then(createdProduct.getName()).isEqualTo("tumbler");
            then(createdProduct.getPrice().isGraterThanEquals("1000")).isTrue();
            then(createdProduct.getQuantity()).isGreaterThan(0);
            then(createdProduct.getCreatedAt()).isNotNull();
            then(createdProduct.getUpdatedAt()).isNotNull();
        }
    }

    @DisplayName("If there is a product with the same code")
    @Nested
    class already_exist_product_same_code {
        String code;

        @BeforeEach
        void before() {
            code = Arbitraries.strings().numeric().ofLength(6).sample();

            CreateProductCommand command = sut.giveMeBuilder(CreateProductCommand.class)
                    .set("code", code).set("name", "tumbler")
                    .set("price", Arbitraries.integers().between(1000, 1000000).sample().toString())
                    .set("quantity", Arbitraries.integers().between(1, 100)).sample();

            createProductUseCase.execute(command);
        }

        @DisplayName("The same product code cannot be generated.")
        @Test
        void throwAlreadyExistProductSameCode() {
            CreateProductCommand command = sut.giveMeBuilder(CreateProductCommand.class)
                    .set("code", code).set("name", "Other")
                    .set("price", Arbitraries.integers().between(1000, 1000000).sample().toString())
                    .set("quantity", Arbitraries.integers().between(1, 100)).sample();

            thenThrownBy(() -> createProductUseCase.execute(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageMatching("There is a product of the same product code.");
        }
    }

    @Test
    void commandCodeShouldNotNull() {
        CreateProductCommand command = sut.giveMeBuilder(CreateProductCommand.class).setNull("code")
                .set("name", "tumbler")
                .set("price", Arbitraries.integers().between(1000, 1000000).sample().toString())
                .set("quantity", Arbitraries.integers().between(1, 100)).sample();

        thenThrownBy(() -> command.validate()).isInstanceOf(ConstraintViolationException.class)
                .hasMessageMatching("code: (?s).*");
    }

    @Test
    void commandNameShouldNotNull() {
        CreateProductCommand command = sut.giveMeBuilder(CreateProductCommand.class)
                .set("code", Arbitraries.strings().numeric().ofLength(6)).setNull("name")
                .set("price", Arbitraries.integers().between(1000, 1000000).sample().toString())
                .set("quantity", Arbitraries.integers().between(1, 100)).sample();

        thenThrownBy(() -> command.validate()).isInstanceOf(ConstraintViolationException.class)
                .hasMessageMatching("name: (?s).*");
    }

    @DisplayName("The price of the product should be bigger than 1.")
    @ParameterizedTest
    @ValueSource(strings = { "-1", "-999" })
    void commandPriceShouldNumber(String price) {
        CreateProductCommand command = sut.giveMeBuilder(CreateProductCommand.class)
                .set("code", Arbitraries.strings().numeric().ofLength(6)).set("name", "tumbler")
                .set("price", price).set("quantity", Arbitraries.integers().between(1, 100))
                .sample();

        thenThrownBy(() -> command.validate()).isInstanceOf(ConstraintViolationException.class)
                .hasMessageMatching("price: (?s).*");
    }

    @DisplayName("The quantity of the product should be greater than zero.")
    @ParameterizedTest
    @ValueSource(ints = { -1, -999 })
    void commandQuantityShouldNumber(int quantity) {
        CreateProductCommand command = sut.giveMeBuilder(CreateProductCommand.class)
                .set("code", Arbitraries.strings().numeric().ofLength(6)).set("name", "tumbler")
                .set("price", Arbitraries.integers().between(1000, 1000000).sample().toString())
                .set("quantity", quantity).sample();

        thenThrownBy(() -> command.validate()).isInstanceOf(ConstraintViolationException.class)
                .hasMessageMatching("quantity: (?s).*");
    }
}
