package com.coderhglee.eshop.products.application;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.coderhglee.eshop.products.assembler.ProductAssembler;
import com.coderhglee.eshop.products.domain.repository.IProductRepository;
import com.coderhglee.eshop.products.dto.ProductDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FindAllProductService implements FindAllProductUseCase {

    private final IProductRepository productRepository;

    @Override
    public List<ProductDto> execute() {
        return this.productRepository.findByAll().stream().map(ProductAssembler::toDto)
                .collect(Collectors.toUnmodifiableList());
    }

}
