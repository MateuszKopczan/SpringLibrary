package com.example.library.service.emailService.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Data
@Component
@EnableConfigurationProperties
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "mail")
public class EmailProperties {

    @NotNull
    private String host;

    @NotNull
    private int port;

    @NotNull
    private String username;

    @NotNull
    private String password;

    @NotNull
    private String transportProtocol;

    @NotNull
    private String smtpAuth;

    @NotNull
    private String smtpStarttlsEnable;

    @NotNull
    private String debug;
}
