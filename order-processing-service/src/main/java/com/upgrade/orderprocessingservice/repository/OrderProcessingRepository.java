package com.upgrade.orderprocessingservice.repository;

import com.upgrade.orderprocessingservice.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderProcessingRepository extends MongoRepository<Order, String> {


}
