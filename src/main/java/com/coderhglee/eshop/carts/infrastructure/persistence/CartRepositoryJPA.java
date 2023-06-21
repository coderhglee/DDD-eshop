package com.coderhglee.eshop.carts.infrastructure.persistence;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import com.coderhglee.eshop.carts.domain.Cart;

public interface CartRepositoryJPA extends CrudRepository<Cart, UUID> {

}
