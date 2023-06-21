package com.coderhglee.eshop.products.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Component;
import com.coderhglee.eshop.products.domain.Product;
import com.coderhglee.eshop.products.domain.repository.IProductRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductRepository implements IProductRepository {

    private final ProductRepositoryJPA productRepositoryJPA;

    @Override
    public Product save(Product product) {
        return this.productRepositoryJPA.save(product);
    }

    @Override
    public Product findByCode(String code) {
        return this.productRepositoryJPA.findByCode(code);
    }

    @Override
    public Optional<Product> findById(String id) {
        return this.productRepositoryJPA.findById(UUID.fromString(id));
    }

    @Override
    public boolean decreaseQuantity(String id, int quantity) {
        int resultCount = this.productRepositoryJPA.decreaseQuantity(UUID.fromString(id), quantity);

        return resultCount > 0;
    }

    @Override
    public List<Product> findByAll() {
        return StreamSupport.stream(this.productRepositoryJPA.findAll().spliterator(), false)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public boolean increaseQuantity(String id, int quantity) {
        int resultCount = this.productRepositoryJPA.increaseQuantity(UUID.fromString(id), quantity);

        return resultCount > 0;
    }
}
