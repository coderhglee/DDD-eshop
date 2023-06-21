package com.coderhglee.eshop.carts.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import com.coderhglee.eshop.common.BaseEntity;
import com.coderhglee.eshop.core.AggregateRoot;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Entity
@AllArgsConstructor
@Getter
public class Cart extends BaseEntity implements AggregateRoot {
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cart_id")
    private List<CartLineItem> cartLines = new ArrayList<>();

    @NotNull
    private String customerId;

    protected Cart() {}


    public Cart(String customerId) {
        this.customerId = customerId;
    }


    public static Cart from(String customerId) {
        return new Cart(customerId);
    }

    public List<CartLineItem> getCartLines() {
        return Collections.unmodifiableList(this.cartLines);
    }

    public void addCartLine(String productId, int quantity) {
        this.foundSameCartLineByProductId(productId).ifPresentOrElse(cartItem -> {
            cartItem.addQuantity(quantity);
        }, () -> {
            this.cartLines.add(new CartLineItem(productId, quantity));
        });;
    }

    private Optional<CartLineItem> foundSameCartLineByProductId(String productId) {
        return this.cartLines.stream().filter(cartItem -> cartItem.isSameProductId(productId))
                .findFirst();
    }


    public boolean isOnwer(String customerId) {
        return this.customerId.equals(customerId);
    }
}
