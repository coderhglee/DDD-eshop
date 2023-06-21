package com.coderhglee.eshop.products.application;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.coderhglee.eshop.products.domain.repository.IProductRepository;
import com.coderhglee.eshop.products.dto.ReduceProductDto;
import com.coderhglee.eshop.products.exception.SoldOutException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReduceProductsStockService implements ReduceProductsStockUseCase {

    private final IProductRepository productRepository;

    @Transactional
    @Override
    public void execute(ReduceProductsStockCommand command) {
        command.validate();

        List<ReduceProductDto> sortedProduct = command.getProducts().stream()
                .sorted(Comparator.comparing(ReduceProductDto::getProductId))
                .collect(Collectors.toUnmodifiableList());

        sortedProduct.forEach((productDto) -> {
            boolean reduceProductStockResult = this.productRepository
                    .decreaseQuantity(productDto.getProductId(), productDto.getQuantity());

            if (!reduceProductStockResult) {
                throw new SoldOutException("SoldOutException occurs.The product ordered is larger than inventory.");
            }
        });
    }
}
