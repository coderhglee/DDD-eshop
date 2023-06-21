package com.coderhglee.eshop.carts.application;

import org.springframework.stereotype.Service;
import com.coderhglee.eshop.carts.assembler.CartAssembler;
import com.coderhglee.eshop.carts.domain.Cart;
import com.coderhglee.eshop.carts.domain.ICartRepository;
import com.coderhglee.eshop.carts.dto.CartDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CreateCartService implements CreateCartUseCase {

    private final ICartRepository cartRepository;

    @Override
    public CartDto execute(CreateCartCommand command) {
        command.validate();

        Cart cart = Cart.from(command.getCustomerId());

        cartRepository.save(cart);

        return CartAssembler.toDto(cart);
    }

}
