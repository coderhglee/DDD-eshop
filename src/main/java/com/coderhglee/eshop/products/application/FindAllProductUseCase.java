package com.coderhglee.eshop.products.application;

import java.util.List;
import com.coderhglee.eshop.products.dto.ProductDto;

public interface FindAllProductUseCase {
    List<ProductDto> execute();
}
