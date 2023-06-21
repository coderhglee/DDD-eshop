package com.coderhglee.eshop.orders.domain;

import java.util.Optional;

public interface IOrderRepository {
    Order save(Order order);

    Optional<Order> findById(String id);
}
