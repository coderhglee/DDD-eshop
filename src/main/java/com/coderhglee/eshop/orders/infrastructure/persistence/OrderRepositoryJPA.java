package com.coderhglee.eshop.orders.infrastructure.persistence;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import com.coderhglee.eshop.orders.domain.Order;

public interface OrderRepositoryJPA extends CrudRepository<Order, UUID> {

}
