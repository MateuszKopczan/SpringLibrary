package com.example.library.payu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.Date;
import java.util.List;

@Data
@Jacksonized @Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderStatusResponse {

    private Order order;
    private Date localReceiptDateTime;
    private PayUProperties[] properties;

    @Data
    @Jacksonized @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Order {

        private String orderId;
        private String extOrderId;
        private Date orderCreateDate;
        private String currencyCode;
        private String totalAmount;
        private OrderCreateRequest.PayUBuyer buyer;
        private PayMethod payMethod;
        private List<OrderCreateRequest.Product> products;
        private String status;
    }

    @Data
    @Jacksonized @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PayUProperties {

        private String name;
        private String value;
    }

    @Data
    @Jacksonized @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PayMethod{

        private String type;

    }
}
