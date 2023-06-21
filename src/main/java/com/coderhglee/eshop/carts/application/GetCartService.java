package com.coderhglee.eshop.carts.application;

import org.springframework.stereotype.Service;
import com.coderhglee.eshop.carts.assembler.CartAssembler;
import com.coderhglee.eshop.carts.domain.Cart;
import com.coderhglee.eshop.carts.domain.ICartRepository;
import com.coderhglee.eshop.carts.dto.CartDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GetCartService implements GetCartUseCase {

    private final ICartRepository cartRepository;

    @Override
    public CartDto findById(String id) {
        Cart foundCart = this.cartRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("not found shopping cart."));

        return CartAssembler.toDto(foundCart);
    }
}

