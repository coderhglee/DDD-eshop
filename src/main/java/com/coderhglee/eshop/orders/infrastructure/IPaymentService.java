package com.coderhglee.eshop.orders.infrastructure;

public interface IPaymentService {
    String requsetPayment(String orderId, String customerId, String paymentAmount);

    boolean checkCompletePayment(String paymentId);
}
