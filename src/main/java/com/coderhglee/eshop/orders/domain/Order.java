package com.coderhglee.eshop.orders.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import com.coderhglee.eshop.common.BaseEntity;
import com.coderhglee.eshop.common.Money;
import com.coderhglee.eshop.core.AggregateRoot;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Entity(name = "order_table")
@AllArgsConstructor
@Getter
public class Order extends BaseEntity implements AggregateRoot {
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderLineItem> orderLines = new ArrayList<>();

    @NotNull
    private String customerId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Embedded
    private Money calculatedTotalAmount;

    @Embedded
    private Money shippingFee;

    private String paymentId;

    protected Order() {}

    private Order(String customerId, OrderStatus status) {
        this.customerId = customerId;
        this.status = status;
    }

    public static Order createDraftOrder(String customerId) {
        return new Order(customerId, OrderStatus.DRAFT);
    }

    private void shouldCheckoutAvaliable() {
        if (!status.equals(OrderStatus.DRAFT)) {
            throw new IllegalStateException("order status is not draft, cannot checkout");
        }

    }

    private void applyTotalPriceFromLineItem() {
        Money amount = this.orderLines.stream().map(OrderLineItem::totalAmount)
                .reduce(Money.ZERO_MONEY, Money::add);

        if (amount.isLessThanEquals("0")) {
            throw new IllegalStateException("amount is required grather then zero");
        }

        this.calculatedTotalAmount = amount;
    }

    private void calculateShippingPrice() {
        if (this.calculatedTotalAmount.isLessThanEquals("0")) {
            throw new IllegalStateException("amount is required to shipping fee");
        }

        if (this.calculatedTotalAmount.isLessThanEquals("50000")) {
            this.shippingFee = Money.of("2500");
        } else {
            this.shippingFee = Money.ZERO_MONEY;
        }
    }

    public void checkout() {
        this.shouldCheckoutAvaliable();

        this.applyTotalPriceFromLineItem();

        this.calculateShippingPrice();
    }

    public void complete() {
        this.status = OrderStatus.COMPLEATE;
    }

    public boolean isComplete() {
        return this.status.equals(OrderStatus.COMPLEATE);
    }

    public boolean isOnwer(String customerId) {
        return this.customerId.equals(customerId);
    }

    public void addAllLineItems(List<OrderLineItem> lineItems) {
        this.orderLines.addAll(lineItems);
    }

    public void requsetPayment(String requestPaymentId) {
        this.paymentId = requestPaymentId;
    }

    public Money getPaymentAmount() {
        return this.calculatedTotalAmount.add(this.shippingFee);
    }

    public String getPaymentAmountToString() {
        return this.calculatedTotalAmount.add(this.shippingFee).toString();
    }

    public String getAmountToString() {
        return this.calculatedTotalAmount.getAmountToString();
    }

    public String getShippingFeeToString() {
        return this.shippingFee.getAmountToString();
    }
}
