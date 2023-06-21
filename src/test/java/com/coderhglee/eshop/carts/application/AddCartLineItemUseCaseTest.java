package com.coderhglee.eshop.carts.application;

import static org.assertj.core.api.BDDAssertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.coderhglee.eshop.carts.domain.Cart;
import com.coderhglee.eshop.carts.domain.CartLineItem;
import com.coderhglee.eshop.carts.domain.ICartRepository;
import com.coderhglee.eshop.carts.dto.CartDto;
import com.coderhglee.eshop.products.application.GetProductUseCase;
import com.coderhglee.eshop.products.application.ProductStockAvailabilityUseCase;
import com.coderhglee.eshop.products.dto.ProductDto;
import com.coderhglee.eshop.products.exception.SoldOutException;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.coderhglee.eshop.IntegrationTest;
import com.coderhglee.eshop.utils.TestUtils;
import net.jqwik.api.Arbitraries;

public class AddCartLineItemUseCaseTest extends IntegrationTest {

    @Autowired
    AddCartLineItemUseCase addCartLineItemUseCase;

    @Autowired
    ICartRepository cartRepository;

    @MockBean
    ProductStockAvailabilityUseCase productStockAvailabilityUseCase;

    @MockBean
    GetProductUseCase getProductUseCase;


    @Nested
    @DisplayName("생성된 카트가 존재하는 경우")
    class is_exist_cart {
        Cart cart;
        ProductDto product;
        AddCartLineItemCommand addCartLineItemCommand;

        @BeforeEach
        void before() {
            FixtureMonkey sut = FixtureMonkey.builder()
                    .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
                    .build();

            product = sut.giveMeBuilder(ProductDto.class)
                    .set("id", TestUtils.generateUUIDToString())
                    .set("code", Arbitraries.strings().numeric().ofLength(6))
                    .set("price", Arbitraries.integers().between(1000, 1000000).sample().toString())
                    .set("quantity", 10).sample();

            cart = cartRepository.save(
                    sut.giveMeBuilder(Cart.class).set("cartLines", new ArrayList<>()).sample());

            addCartLineItemCommand = sut.giveMeBuilder(AddCartLineItemCommand.class)
                    .set("cartId", cart.getStringId()).set("customerId", cart.getCustomerId())
                    .set("productCode", product.getCode()).set("quantity", 10).sample();

            when(productStockAvailabilityUseCase.checkAvailableForPurchase(anyString(), anyInt()))
                    .thenReturn(product);

            when(getProductUseCase.findByCode(anyString())).thenReturn(product);
        }

        @DisplayName("Cartitem is added to the cart.")
        @Test
        void success_add_cart_item() {
            CartDto cart = addCartLineItemUseCase.execute(addCartLineItemCommand);

            then(cart).isNotNull();

            then(cart.getCartLineItemDtos()).extracting("productId", "quantity")
                    .contains(tuple(product.getId(), addCartLineItemCommand.getQuantity()));
        }
    }

    @Nested
    @DisplayName("When the CartTem of the same product exists in the generated cart")
    class is_same_product_id_cart_item {
        Cart cart;
        ProductDto product;
        AddCartLineItemCommand addCartLineItemCommand;

        @BeforeEach
        void before() {
            FixtureMonkey sut = FixtureMonkey.builder()
                    .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
                    .build();

            product = sut.giveMeBuilder(ProductDto.class)
                    .set("id", TestUtils.generateUUIDToString())
                    .set("code", Arbitraries.strings().numeric().ofLength(6))
                    .set("price", Arbitraries.integers().between(1000, 1000000).sample().toString())
                    .set("quantity", 10).sample();

            cart = cartRepository.save(sut.giveMeBuilder(Cart.class)
                    .set("cartLines", Arrays.asList(sut.giveMeBuilder(CartLineItem.class)
                            .set("id", TestUtils.generateUUID()).set("productId", product.getId())
                            .set("quantity", 10).sample()))
                    .sample());

            addCartLineItemCommand = sut.giveMeBuilder(AddCartLineItemCommand.class)
                    .set("cartId", cart.getStringId()).set("customerId", cart.getCustomerId())
                    .set("productCode", product.getCode()).set("quantity", 10).sample();

            when(productStockAvailabilityUseCase.checkAvailableForPurchase(anyString(), anyInt()))
                    .thenReturn(product);

            when(getProductUseCase.findByCode(anyString())).thenReturn(product);
        }

        @DisplayName("Quantity is added to the CartTem.")
        @Test
        void success_add_cart_item() {
            CartDto cart = addCartLineItemUseCase.execute(addCartLineItemCommand);

            then(cart).isNotNull();

            then(cart.getCartLineItemDtos()).extracting("productId", "quantity")
                    .contains(tuple(product.getId(), 20));
        }
    }

    @Test
    @DisplayName("An error occurs if the generated cart does not exist.")
    void whenNotExistCartThrowError() {
        FixtureMonkey sut = FixtureMonkey.builder()
                .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE).build();

        thenThrownBy(() -> addCartLineItemUseCase.execute(sut
                .giveMeBuilder(AddCartLineItemCommand.class)
                .set("cartId", TestUtils.generateUUIDToString()).set("quantity", 10).sample()))
                        .isInstanceOf(NullPointerException.class);
    }


    @Test
    @DisplayName("SoldoutException occurs if there is a lack of quantity of the product.")
    void whenProductQuantityLessThenZeroThrowSoldOutException() {
        FixtureMonkey sut = FixtureMonkey.builder()
                .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE).build();

        Cart cart = cartRepository
                .save(sut.giveMeBuilder(Cart.class).set("cartLines", new ArrayList<>()).sample());

        ProductDto product = sut.giveMeBuilder(ProductDto.class)
                .set("id", TestUtils.generateUUIDToString()).sample();

        AddCartLineItemCommand addCartItemCommand = sut.giveMeBuilder(AddCartLineItemCommand.class)
                .set("cartId", cart.getStringId()).set("customerId", cart.getCustomerId())
                .set("productCode", product.getCode()).set("quantity", 10).sample();

        when(getProductUseCase.findByCode(product.getCode())).thenReturn(product);
        when(productStockAvailabilityUseCase.checkAvailableForPurchase(product.getId(),
                addCartItemCommand.getQuantity())).thenThrow(new SoldOutException());


        thenThrownBy(() -> addCartLineItemUseCase.execute(addCartItemCommand))
                .isInstanceOf(SoldOutException.class);
    }

    @Test
    @DisplayName("If there is no matching product, the NullPointexception occurs.")
    void whenProductNotExistThrowNullPointException() {
        FixtureMonkey sut = FixtureMonkey.builder()
                .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE).build();

        Cart cart = cartRepository
                .save(sut.giveMeBuilder(Cart.class).set("cartLines", new ArrayList<>()).sample());

        AddCartLineItemCommand addCartItemCommand =
                sut.giveMeBuilder(AddCartLineItemCommand.class).set("cartId", cart.getStringId())
                        .set("customerId", cart.getCustomerId()).set("quantity", 10).sample();

        when(getProductUseCase.findByCode(anyString())).thenReturn(null);

        thenThrownBy(() -> addCartLineItemUseCase.execute(addCartItemCommand))
                .isInstanceOf(NullPointerException.class);
    }
}
