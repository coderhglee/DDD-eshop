package com.coderhglee.eshop.carts.assembler;

import java.util.List;
import java.util.stream.Collectors;
import com.coderhglee.eshop.carts.domain.Cart;
import com.coderhglee.eshop.carts.dto.CartDto;
import com.coderhglee.eshop.carts.dto.CartLineItemDto;

public class CartAssembler {
    public static CartDto toDto(Cart cart) {
        List<CartLineItemDto> lineItems = cart.getCartLines().stream().map((cartLineItem) -> {
            return new CartLineItemDto(cartLineItem.getProductId(), cartLineItem.getQuantity());
        }).collect(Collectors.toUnmodifiableList());

        return new CartDto(cart.getStringId(), cart.getCustomerId(), lineItems);
    }
}
