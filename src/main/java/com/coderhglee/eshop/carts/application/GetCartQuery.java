package com.coderhglee.eshop.carts.application;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GetCartQuery {
    private final String cartId;
}
