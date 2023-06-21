package com.coderhglee.eshop.products.domain.repository;

import java.util.List;
import java.util.Optional;
import com.coderhglee.eshop.products.domain.Product;

public interface IProductRepository {
    Product save(Product product);

    Optional<Product> findById(String id);

    Product findByCode(String code);

    List<Product> findByAll();

    boolean decreaseQuantity(String id, int quantity);

    boolean increaseQuantity(String id, int quantity);
}
