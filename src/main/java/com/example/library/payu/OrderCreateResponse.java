package com.example.library.payu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized @Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderCreateResponse {

    private PayUResponseStatus status;
    private String redirectUri;
    private String orderId;


    @Data
    @Jacksonized @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PayUResponseStatus {

        private String statusCode;

    }
}

