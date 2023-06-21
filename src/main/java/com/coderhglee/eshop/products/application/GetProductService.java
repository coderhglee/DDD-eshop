package com.coderhglee.eshop.products.application;

import org.springframework.stereotype.Service;
import com.coderhglee.eshop.products.assembler.ProductAssembler;
import com.coderhglee.eshop.products.domain.Product;
import com.coderhglee.eshop.products.domain.repository.IProductRepository;
import com.coderhglee.eshop.products.dto.ProductDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetProductService implements GetProductUseCase {

    private final IProductRepository productRepository;

    @Override
    public ProductDto findById(String id) {
        Product product = this.productRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("상품을 찾을 수 없습니다."));

        return ProductAssembler.toDto(product);
    }

    @Override
    public ProductDto findByCode(String code) {
        Product product = this.productRepository.findByCode(code);

        if (product == null) {
            throw new NullPointerException("상품을 찾을 수 없습니다.");
        }
        return ProductAssembler.toDto(product);
    }
}
