package com.coderhglee.eshop.carts.domain;

import java.util.Optional;

public interface ICartRepository {

    Cart save(Cart cart);

    Optional<Cart> findById(String id);
}
