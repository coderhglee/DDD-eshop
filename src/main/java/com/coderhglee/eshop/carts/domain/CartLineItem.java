package com.coderhglee.eshop.carts.domain;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import com.coderhglee.eshop.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Entity
public class CartLineItem extends BaseEntity {
    @NotNull
    private String productId;

    @NotNull
    private int quantity;

    protected CartLineItem() {}

    public boolean isSameProductId(String targetProductId) {
        return this.productId.equals(targetProductId);
    }

    public void addQuantity(int addedQuantity) {
        this.quantity += addedQuantity;
    }
}
