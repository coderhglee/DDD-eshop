package com.coderhglee.eshop.products.exception;

public class SoldOutException extends IllegalStateException {
    public SoldOutException() {
        super();
    }

    public SoldOutException(String message) {
        super(message);
    }
}
