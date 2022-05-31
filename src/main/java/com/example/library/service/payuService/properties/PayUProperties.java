package com.example.library.service.payuService.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Component
@EnableConfigurationProperties
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "payu")
public class PayUProperties {

    @NotEmpty
    private String description;

    @NotNull
    private String merchantPosId;

    @NotNull
    private String secondKey;

    @NotNull
    private String clientId;

    @NotEmpty
    private String url;

    @NotEmpty
    private String continueUrl;

    @NotEmpty
    private String notifyUrl;

    @NotEmpty
    private String authorizationUrl;

    @NotNull
    private String clientSecret;

}
