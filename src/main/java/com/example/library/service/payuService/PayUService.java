package com.example.library.service.payuService;

import com.example.library.entity.Book;
import com.example.library.entity.Order;
import com.example.library.payu.OrderCreateResponse;
import com.example.library.payu.OrderRefundResponse;
import com.example.library.payu.OrderStatusResponse;

import java.util.Map;

public interface PayUService {

    String getAuthorizationToken();
    OrderCreateResponse createNewOrder(Order order, Map<Book, Integer> map, String userEmail);
    OrderStatusResponse checkPaymentStatus();
    OrderRefundResponse createRefund(Order order);
    boolean verifySignature(String header, String body);
}
