package com.coderhglee.eshop.carts.application;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.coderhglee.eshop.carts.assembler.CartAssembler;
import com.coderhglee.eshop.carts.domain.Cart;
import com.coderhglee.eshop.carts.domain.CartLineItem;
import com.coderhglee.eshop.carts.domain.ICartRepository;
import com.coderhglee.eshop.carts.dto.CartDto;
import com.coderhglee.eshop.products.application.GetProductUseCase;
import com.coderhglee.eshop.products.application.ProductStockAvailabilityUseCase;
import com.coderhglee.eshop.products.dto.ProductDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddCartLineItemService implements AddCartLineItemUseCase {

    private final ProductStockAvailabilityUseCase productStockAvailabilityUseCase;
    private final GetProductUseCase getProductUseCase;
    private final ICartRepository cartRepository;

    @Transactional
    @Override
    public CartDto execute(AddCartLineItemCommand command) {
        command.validate();

        Cart cart = cartRepository.findById(command.getCartId())
                .orElseThrow(() -> new NullPointerException("not found shopping cart."));

        if (!cart.isOnwer(command.getCustomerId())) {
            throw new IllegalAccessError("No authority in the shopping cart.");
        }

        ProductDto product = getProductUseCase.findByCode(command.getProductCode());

        cart.addCartLine(product.getId(), command.getQuantity());

        this.checkSoldOutCartItem(cart.getCartLines());

        return CartAssembler.toDto(cart);
    }

    private void checkSoldOutCartItem(List<CartLineItem> cartItem) {
        cartItem.stream().forEach(item -> this.productStockAvailabilityUseCase
                .checkAvailableForPurchase(item.getProductId(), item.getQuantity()));
    }
}
