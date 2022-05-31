package com.example.library.service.emailService;

import com.example.library.entity.Order;
import com.example.library.entity.User;

public interface EmailService {

    String sendVerificationCode(String verifyCode, String email);
    void sendResetPasswordToken(String email, String token);
    void sendOrderConfirmation(Order order);
    void sendOrderWaitingForShipment(Order order);
    void sendOrderShipment(Order order);
    void sendOrderCancel(Order order);
}
