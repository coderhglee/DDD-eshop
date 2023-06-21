package com.coderhglee.eshop.products.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import com.coderhglee.eshop.common.BaseEntity;
import com.coderhglee.eshop.common.Money;
import com.coderhglee.eshop.core.AggregateRoot;
import com.coderhglee.eshop.products.exception.SoldOutException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Product extends BaseEntity implements AggregateRoot {
    @NotNull
    @Column(unique = true)
    private String code;

    @NotNull
    private String name;

    @Embedded
    @NotNull
    private Money price;

    @NotNull
    private int quantity;

    public static Product of(String code, String name, Money price, int quantity) {
        return new Product(code, name, price, quantity);
    }

    public String getPriceToString() {
        return this.price.getAmountToFormatString("#");
    }

    public void checkAvailableForPurchase(int quantity) {
        int remainQuantity = this.quantity - quantity;

        if (remainQuantity < 0) {
            throw new SoldOutException("SoldOutException occurs.The product ordered is larger than inventory.");
        }
    }
}
