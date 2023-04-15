package com.upgrade.orderprocessingservice.controller.advice;

import com.upgrade.orderprocessingservice.controller.model.ErrorVO;
import com.upgrade.orderprocessingservice.exceptions.OrderProcessingFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.upgrade.orderprocessingservice.constants.OrderProcessingConstants.PAYMENT_FAILED_ERROR_CODE;

/**
 * To handle Exceptions globally and deal with them to keep code clean
 */
@ControllerAdvice
public class OrderProcessingControllerAdvice {

    @ExceptionHandler(OrderProcessingFailedException.class)
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    public @ResponseBody ErrorVO handleOrderProcessingException(){

        return ErrorVO
                .builder()
                .errorCode(PAYMENT_FAILED_ERROR_CODE)
                .build();
    }
}
