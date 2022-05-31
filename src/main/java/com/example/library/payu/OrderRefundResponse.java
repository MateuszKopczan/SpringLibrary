package com.example.library.payu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.Date;

@Data
@Jacksonized @Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderRefundResponse {

    private String orderId;
    private Refund refund;
    private Status status;


    @Data
    @Jacksonized @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Refund{

        private String refundId;
        private String extRefundId;
        private String amount;
        private String currencyCode;
        private String description;
        private Date creationDateTime;
        private String status;
        private Date statusDateTime;
    }

    @Data
    @Jacksonized @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Status{

        private String statusCode;
        private String statusDesc;
        private String severity;
        private String code;
        private String codeLiteral;
    }
}
