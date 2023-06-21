package com.coderhglee.eshop.common;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Money {
    public static final Money ZERO_MONEY = ofZero();

    protected Money() {}

    private BigDecimal amount;

    private Money multiply(Money money) {
        return new Money(this.amount.multiply(money.amount, MathContext.DECIMAL128));
    }

    public Money add(Money money) {
        return new Money(this.amount.add(money.amount));
    }

    public Money multiply(int targetNumber) {
        return this.multiply(Money.of(targetNumber));
    }

    public String getAmountToString() {
        return this.amount.toString();
    }

    public String getAmountToFormatString(String pattern) {
        DecimalFormat decimalFormat = new DecimalFormat(pattern);

        return decimalFormat.format(this.amount);
    }

    public int compareTo(Money target) {
        return this.amount.compareTo(target.getAmount());
    }

    public boolean isGraterThan(String target) {
        return this.compareTo(Money.of(target)) > 0;
    }

    public boolean isGraterThanEquals(String target) {
        return this.compareTo(Money.of(target)) >= 0;
    }

    public boolean isLessThanEquals(String target) {
        return this.compareTo(Money.of(target)) <= 0;
    }

    public boolean isLessThan(String target) {
        return this.compareTo(Money.of(target)) < 0;
    }

    public static Money of(String amount) {
        return new Money(new BigDecimal(amount));
    }

    public static Money of(int amount) {
        return new Money(new BigDecimal(amount));
    }

    private static Money ofZero() {
        return new Money(BigDecimal.ZERO);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Money money = (Money) o;
        return amount.equals(money.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
}
