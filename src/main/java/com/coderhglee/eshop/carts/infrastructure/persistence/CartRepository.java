package com.coderhglee.eshop.carts.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import com.coderhglee.eshop.carts.domain.Cart;
import com.coderhglee.eshop.carts.domain.ICartRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CartRepository implements ICartRepository {

    private final CartRepositoryJPA cartRepository;

    @Override
    public Cart save(Cart cart) {
        return this.cartRepository.save(cart);
    }

    @Override
    public Optional<Cart> findById(String id) {
        return this.cartRepository.findById(UUID.fromString(id));
    }

}
