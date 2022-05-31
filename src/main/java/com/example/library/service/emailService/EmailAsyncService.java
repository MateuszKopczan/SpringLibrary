package com.example.library.service.emailService;

import com.example.library.entity.Order;
import com.example.library.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailAsyncService {

    @Autowired
    private EmailService emailService;

    @Async
    public String sendVerificationCode(String verifyCode, String email) {
        return emailService.sendVerificationCode(verifyCode, email);
    }

    @Async
    public void sendResetPasswordToken(String email, String token){
        emailService.sendResetPasswordToken(email, token);
    }

    @Async
    public void sendOrderConfirmation(Order order){
        emailService.sendOrderConfirmation(order);
    }


    @Async
    public void sendOrderWaitingForShipment(Order order){
        emailService.sendOrderWaitingForShipment(order);
    }

    @Async
    public void sendOrderShipment(Order order){
        emailService.sendOrderShipment(order);
    }

    @Async
    public void sendOrderCancel(Order order){
        emailService.sendOrderCancel(order);
    }
}
