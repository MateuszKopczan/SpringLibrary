package com.example.library.payu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;


@Data
@Jacksonized @Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderCreateRequest {

    private String extOrderId;
    private String notifyUrl;
    private String continueUrl;
    private String customerIp;
    private String merchantPosId;
    private String validityTime;
    private String description;
    private String additionalDescription;
    private String visibleDescription;
    private String statementDescription;
    private String currencyCode;
    private String totalAmount;
    private PayUBuyer buyer;
    private List<Product> products;


    @Data
    @Jacksonized @Builder
    public static class Product{

        private String name;
        private String unitPrice;
        private String quantity;

    }


    @Data
    @Jacksonized @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PayUBuyer{

        private String email;
        private String phone;
        private String firstName;
        private String lastName;
        private String language;
        private PayUDelivery delivery;

    }

    @Data
    @Jacksonized @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PayUDelivery {

        private String street;
        private String postalCode;
        private String city;
        private String countryCode;

    }
}


