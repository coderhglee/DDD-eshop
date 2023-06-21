package com.coderhglee.eshop.products.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.coderhglee.eshop.products.domain.repository.IProductRepository;
import com.coderhglee.eshop.products.exception.SoldOutException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReduceProductStockService implements ReduceProductStockUseCase {

    private final IProductRepository productRepository;

    @Transactional
    @Override
    public void execute(ReduceProductStockCommand command) {
        command.validate();

        boolean reduceProductStockResult = this.productRepository
                .decreaseQuantity(command.getProductId(), command.getQuantity());

        if (!reduceProductStockResult) {
            throw new SoldOutException("SoldOutException occurs.The product ordered is larger than inventory.");
        }
    }
}
