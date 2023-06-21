package com.coderhglee.eshop.orders.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import com.coderhglee.eshop.orders.domain.IOrderRepository;
import com.coderhglee.eshop.orders.domain.Order;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderRepository implements IOrderRepository {

    private final OrderRepositoryJPA orderRepositoryJPA;

    @Override
    public Order save(Order order) {
        return this.orderRepositoryJPA.save(order);
    }

    @Override
    public Optional<Order> findById(String id) {
        return this.orderRepositoryJPA.findById(UUID.fromString(id));
    }

}
