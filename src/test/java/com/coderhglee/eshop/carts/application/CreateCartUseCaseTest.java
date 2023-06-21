package com.coderhglee.eshop.carts.application;

import static org.assertj.core.api.BDDAssertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.coderhglee.eshop.carts.domain.ICartRepository;
import com.coderhglee.eshop.carts.dto.CartDto;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.coderhglee.eshop.IntegrationTest;
import com.coderhglee.eshop.utils.TestUtils;

public class CreateCartUseCaseTest extends IntegrationTest {

    @Autowired
    ICartRepository cartRepository;

    @Autowired
    CreateCartUseCase createCartUseCase;

    @Test
    @DisplayName("You can create a cart.")
    void shouldCreateCart() {
        FixtureMonkey sut = FixtureMonkey.builder()
                .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE).build();

        CreateCartCommand command = sut.giveMeBuilder(CreateCartCommand.class)
                .set("customerId", TestUtils.generateUUIDToString()).sample();

        CartDto cart = createCartUseCase.execute(command);

        then(cart).isNotNull();
    }

}
