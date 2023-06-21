package com.coderhglee.eshop.products.infrastructure.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import com.coderhglee.eshop.products.domain.Product;

public interface ProductRepositoryJPA extends CrudRepository<Product, UUID> {
    Product findByCode(String code);

    @Modifying()
    @Query("UPDATE Product p SET p.quantity = p.quantity - :quantity WHERE p.id = :productId AND p.quantity >= :quantity")
    int decreaseQuantity(@Param("productId") UUID productId, @Param("quantity") int quantity);

    @Modifying()
    @Query("UPDATE Product p SET p.quantity = p.quantity + :quantity WHERE p.id = :productId")
    int increaseQuantity(@Param("productId") UUID productId, @Param("quantity") int quantity);
}
