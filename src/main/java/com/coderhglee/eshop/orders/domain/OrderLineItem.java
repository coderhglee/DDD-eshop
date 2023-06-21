package com.coderhglee.eshop.orders.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import com.coderhglee.eshop.common.BaseEntity;
import com.coderhglee.eshop.common.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Entity
public class OrderLineItem extends BaseEntity {
    @NotNull
    private String productId;

    @NotNull
    private String productName;

    @NotNull
    private int quantity;

    @Embedded
    @NotNull
    private Money originPrice;

    protected OrderLineItem() {}

    public Money totalAmount() {
        return originPrice.multiply(quantity);
    }

    public String getOriginPriceToString() {
        return originPrice.getAmountToString();
    }

    public static OrderLineItem of(String productId, String productName, int quantity,
            String originPrice) {
        return new OrderLineItem(productId, productName, quantity, Money.of(originPrice));
    }
}
