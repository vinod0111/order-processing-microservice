package com.upgrade.orderprocessingservice.feign;

import com.upgrade.orderprocessingservice.exceptions.OrderProcessingFailedException;
import com.upgrade.orderprocessingservice.model.PaymentResponseVO;
import org.springframework.stereotype.Component;

@Component
public class PaymentClientFallback implements PaymentClient{
    @Override
    public PaymentResponseVO getPaymentStatus(String orderId) throws OrderProcessingFailedException {
         throw new OrderProcessingFailedException();
    }
}
