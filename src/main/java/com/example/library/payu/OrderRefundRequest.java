package com.example.library.payu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized @Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderRefundRequest {

    private Refund refund;

    @Data
    @Jacksonized @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Refund{

        private String description;
        private int amount;

    }
}
