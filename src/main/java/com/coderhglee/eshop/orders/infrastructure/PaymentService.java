package com.coderhglee.eshop.orders.infrastructure;

import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PaymentService implements IPaymentService {

    @Override
    public String requsetPayment(String orderId, String customerId, String paymentAmount) {
        return UUID.randomUUID().toString();
    }

    @Override
    public boolean checkCompletePayment(String paymentId) {
        return true;
    }

}
