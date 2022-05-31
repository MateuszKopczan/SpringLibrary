package com.example.library.config;

import com.example.library.service.emailService.properties.EmailProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Properties;

@Configuration
@EnableAsync
public class Config {

    @Autowired
    private EmailProperties emailProperties;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailProperties.getHost());
        mailSender.setPort(emailProperties.getPort());
        mailSender.setUsername(emailProperties.getUsername());
        mailSender.setPassword(emailProperties.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", emailProperties.getTransportProtocol());
        props.put("mail.smtp.auth", emailProperties.getSmtpAuth());
        props.put("mail.smtp.starttls.enable", emailProperties.getSmtpStarttlsEnable());
        props.put("mail.debug", emailProperties.getDebug());

        return mailSender;
    }
}
