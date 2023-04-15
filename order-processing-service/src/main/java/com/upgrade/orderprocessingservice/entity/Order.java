package com.upgrade.orderprocessingservice.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class Order {

    private String orderId;
    private String orderStatus;
    private Double orderAmount;
    private String paymentReferenceNumber;

}
