package com.coderhglee.eshop.common;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

@Convert
public class MoneyConverter implements AttributeConverter<Money, String> {

    @Override
    public String convertToDatabaseColumn(Money attribute) {
        return attribute.getAmountToString();
    }

    @Override
    public Money convertToEntityAttribute(String dbData) {
        return Money.of(dbData);
    }

}
