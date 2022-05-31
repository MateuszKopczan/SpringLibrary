package com.example.library.controller;

import com.example.library.entity.Book;
import com.example.library.entity.Order;
import com.example.library.payu.OrderCreateResponse;
import com.example.library.service.MainService;
import com.example.library.service.orderService.OrderService;
import com.example.library.service.payuService.PayUService;
import com.example.library.service.userService.UserService;
import com.example.library.config.userDetails.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/checkout")
public class PayUController {

    @Autowired
    private PayUService payUService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/process")
    public String processPayment(Model model){
        if(!model.containsAttribute("order") ||
            !model.containsAttribute("map"))
            return "error/404";
        Order order = (Order)model.getAttribute("order");
        Map<Book, Integer> map = (Map<Book, Integer>) model.getAttribute("map");

        OrderCreateResponse orderCreateResponse = payUService.createNewOrder(order, map, order.getUser().getEmail());
        if(orderCreateResponse.getStatus().getStatusCode().equals("SUCCESS")){
           order.setPayUOrderId(orderCreateResponse.getOrderId());
           orderService.save(order);
        }
        else
            return "error/404";

        return "redirect:" + orderCreateResponse.getRedirectUri();
    }

    @GetMapping("/callback")
    public String callbackPayment(final @RequestParam Optional<String> error, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Order order = orderService.findLatestByUserId(userDetails.getUser().getId());
        return "redirect:/account/orders/" + order.getId();
    }
}
