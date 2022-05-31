package com.example.library.service.emailService;

import com.example.library.dao.userDAO.UserDAO;
import com.example.library.entity.Order;
import com.example.library.entity.User;
import com.example.library.service.userService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Random;
import java.util.UUID;

@Service
public class EmailServiceImpl implements EmailService{

    private final String shopEmail = "shop@springlibrary.com";
    private final String resetPasswordLink = "127.0.0.1:8080/account/changePassword?token=";
    private final String orderDetailsLink = "127.0.0.1:8080/account/orders/";

    @Autowired
    private JavaMailSender emailSender;

//    @Autowired
//    private UserService userService;

    @Override
    public String sendVerificationCode(String verifyCode, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(shopEmail);
        message.setTo(email);
        message.setSubject("Spring Library Team");
        message.setText("Your verification code: " + verifyCode);

        emailSender.send(message);
        return verifyCode;
    }

    @Override
    public void sendResetPasswordToken(String email, String token) {
        SimpleMailMessage message = prepareEmail(email);
        message.setSubject("Password reset");
        message.setText("Your password reset link: " + resetPasswordLink + token);
        emailSender.send(message);
    }

    @Override
    public void sendOrderConfirmation(Order order) {
        SimpleMailMessage message = prepareEmail(order.getUser().getEmail());
        message.setSubject("Order confirmation");
        StringBuilder sb = new StringBuilder();
        sb.append("Dear Client,\n");
        sb.append("we have just accepted your new order number " + order.getPayUOrderId());
        sb.append("\nWe will keep you updated on the next stages of implementation.\n\n");
        sb.append("\"You can check the current status of your order here: " + orderDetailsLink + order.getId());

        message.setText(sb.toString());
        emailSender.send(message);
    }

    @Override
    public void sendOrderWaitingForShipment(Order order) {
        SimpleMailMessage message = prepareEmail(order.getUser().getEmail());
        message.setSubject("Your order is ready to ship");
        StringBuilder sb = new StringBuilder();
        sb.append("Dear Client,\n");
        sb.append("Your order number " + order.getPayUOrderId() + "  has been packed and ready for shipment.\n\n");
        sb.append("We will email you again when it is shipped.\n\n");
        sb.append("You can check the current status of your order here: " + orderDetailsLink + order.getId());
        message.setText(sb.toString());
        emailSender.send(message);
    }

    @Override
    public void sendOrderShipment(Order order) {
        SimpleMailMessage message = prepareEmail(order.getUser().getEmail());
        message.setSubject("Your order is on its way");
        StringBuilder sb = new StringBuilder();
        sb.append("Dear Client,\n");
        sb.append("Your order number " + order.getPayUOrderId() + " is on its way to the address you provided.\n\n");
        sb.append("You can check the current status of your order here: " + orderDetailsLink + order.getId());
        message.setText(sb.toString());
        emailSender.send(message);
    }

    @Override
    public void sendOrderCancel(Order order) {
        SimpleMailMessage message = prepareEmail(order.getUser().getEmail());
        message.setSubject("Your order has been canceled");
        StringBuilder sb = new StringBuilder();
        sb.append("Dear Client,\n");
        sb.append("Your order number" + order.getPayUOrderId() + " has been canceled.\n");
        sb.append("The money will be returned to you within 3 business days via the same payment method");
        message.setText(sb.toString());
        emailSender.send(message);
    }


    private SimpleMailMessage prepareEmail(String email){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(shopEmail);
        message.setTo(email);
        return message;
    }
}
