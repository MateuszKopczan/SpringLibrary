package com.example.library.controller;

import com.example.library.dto.UserCheckoutDTO;
import com.example.library.entity.*;
import com.example.library.service.MainService;
import com.example.library.service.cartService.CartService;
import com.example.library.service.emailService.EmailAsyncService;
import com.example.library.service.orderService.OrderService;
import com.example.library.service.userService.UserService;
import com.example.library.config.userDetails.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.*;

@RequestMapping("/checkout")
@Controller
public class CheckoutController{

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private EmailAsyncService emailAsyncService;

    @GetMapping("")
    public String listCheckout(Model model, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Cart cart = cartService.findByUserId(userDetails.getUser().getId());
        List<Book> booksInCart = cart.getBooks();
        Map<Book, Integer> map = cartService.createProductsMap(booksInCart);
        if(!model.containsAttribute("crmUserCheckout")){
            UserCheckoutDTO userCheckoutDTO = userService.copyValue(userDetails.getUser());
            model.addAttribute("crmUserCheckout", userCheckoutDTO);
        }

        model.addAttribute("products", booksInCart);
        model.addAttribute("map", map);
        model.addAttribute("user", userDetails.getUser());

        return "account/checkout";
    }

    @PostMapping("")
    public String prepareCheckout(@Valid @ModelAttribute("crmUserCheckout") UserCheckoutDTO userCheckoutDTO,
                                  BindingResult bindingResult, Authentication authentication,
                                  RedirectAttributes redirectAttributes){
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.crmUserCheckout", bindingResult);
            redirectAttributes.addFlashAttribute("crmUserCheckout", userCheckoutDTO);
            return "redirect:/checkout";
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Cart cart = cartService.findByUserId(userDetails.getUser().getId());
        float cartValue = cartService.getCartValue(cart);
        List<Book> products = List.copyOf(cart.getBooks());
        Map<Book, Integer> map = cartService.createProductsMap(products);


        for(Map.Entry<Book, Integer> entry : map.entrySet()){
            if(entry.getKey().getNumberForSale() - entry.getValue() < 0){
                redirectAttributes.addFlashAttribute("message", "One of the products in your cart is temporarily unavailable");
                return "redirect:/checkout";
            }
        }

        for(Book book : products)
            book.setNumberForSale(book.getNumberForSale() - 1);

        Cart emptyCart = new Cart();
        emptyCart.setUser(userDetails.getUser());
        userDetails.getUser().setCart(emptyCart);
        userService.save(userDetails.getUser());
        cartService.deleteById(cart.getId());

        Order order = orderService.createOrder(userCheckoutDTO, userDetails.getUser(), cartValue, products);
        emailAsyncService.sendOrderConfirmation(order);

        redirectAttributes.addFlashAttribute("order", order);
        redirectAttributes.addFlashAttribute("map", map);
        return "redirect:/checkout/process";
    }
}
