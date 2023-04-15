package com.upgrade.orderprocessingservice.service;

import com.upgrade.orderprocessingservice.entity.Order;
import com.upgrade.orderprocessingservice.exceptions.OrderProcessingFailedException;
import com.upgrade.orderprocessingservice.feign.PaymentClient;
import com.upgrade.orderprocessingservice.model.OrderVO;
import com.upgrade.orderprocessingservice.model.OrderResponseVO;
import com.upgrade.orderprocessingservice.model.PaymentResponseVO;
import com.upgrade.orderprocessingservice.repository.OrderProcessingRepository;
import feign.FeignException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import static com.upgrade.orderprocessingservice.constants.OrderProcessingConstants.*;

@Service
@Log4j2
public class OrderProcessingService {

    private OrderProcessingRepository repository;
    private PaymentClient paymentClient;

    @Autowired
    KafkaTemplate kafkaTemplate; // kafka templating

    @Value("${order_processing_topic}") // get environment variable and assign them to one variable
    private String orderProcessingTopic;

    public OrderProcessingService(OrderProcessingRepository repository, PaymentClient paymentClient) {
        this.repository = repository;
        this.paymentClient = paymentClient;
    }

    public OrderResponseVO createOrder(OrderVO orderVO) throws OrderProcessingFailedException {

        /*
        May throw the feign exception from external service
         */
        PaymentResponseVO responseVO = null;
        try {
            log.info("Paymet initiated for orderID: "+orderVO.getOrderId());
            responseVO = paymentClient.getPaymentStatus(orderVO.getOrderId());
        }
        catch (FeignException feignException){
            throw new OrderProcessingFailedException();
        }
//        PaymentResponseVO responseVO = paymentClient.getPaymentStatus(orderVO.getOrderId());

        OrderResponseVO orderResponseVO = OrderResponseVO
                .builder()
                .orderId(orderVO.getOrderId())
                .build();

        if (PAYMENTS_SUCCESS.equals(responseVO.getPaymentStatus())){
            repository.save(Order.builder()
                    .orderId(orderVO.getOrderId())
                    .orderAmount(orderVO.getOrderAmount())
                    .orderStatus(ORDER_CREATED)
                    .paymentReferenceNumber(responseVO.getPaymentReferenceNumber())
                    .build()
            );
            orderResponseVO.setOrderStatus(ORDER_CREATED);
        }
        else {
            repository.save(Order.builder()
                    .orderId(orderVO.getOrderId())
                    .orderAmount(orderVO.getOrderAmount())
                    .orderStatus(ORDER_PROCESSING_FAILED)
                    .build()
            );
            orderResponseVO.setOrderStatus(ORDER_PROCESSING_FAILED);
        }
        //Fetch the user detials correspond to user it should be stateless
        sendNotification(orderResponseVO);
        log.info("ORDER COMPLETE");
        return orderResponseVO;
    }

    /**
     * Send the message to notification server
     * @param orderResponseVO
     */
    private void sendNotification(OrderResponseVO orderResponseVO) {
        kafkaTemplate.send(
                MessageBuilder.withPayload(orderResponseVO)
                        .setHeader(KafkaHeaders.TOPIC, orderProcessingTopic) // set topic from the environment variable
                        .setHeader("EVENT_TYPE", orderResponseVO.getOrderStatus()) // set custom event type to filter out the things in end
                        .build()
        );
    }

    /**
     * quickly test how it will work by adding the listener here only rather then creating listener
     */

    @KafkaListener(topics = "${order_processing_topic}", groupId = "1")
    private void orderProcessingListener(Message<OrderResponseVO> message) {
        System.out.println("Recieved message from kafka : "+message.getPayload());
    }
}
