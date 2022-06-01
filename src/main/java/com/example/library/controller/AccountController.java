package com.example.library.controller;

import com.example.library.dto.PasswordResetDTO;
import com.example.library.dto.UserDTO;
import com.example.library.dto.UserPasswordDTO;
import com.example.library.entity.Book;
import com.example.library.entity.Order;
import com.example.library.entity.Cart;
import com.example.library.entity.OrderStatus;
import com.example.library.entity.User;
import com.example.library.service.MainService;
import com.example.library.service.bookService.BookService;
import com.example.library.service.cartService.CartService;
import com.example.library.service.emailService.EmailAsyncService;
import com.example.library.service.orderService.OrderService;
import com.example.library.service.payuService.PayUService;
import com.example.library.service.userService.UserService;
import com.example.library.config.userDetails.CustomUserDetails;
import com.example.library.validation.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/account")
@Controller
public class AccountController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private BookService bookService;

    @Autowired
    private EmailAsyncService emailAsyncService;

    @Autowired
    private PayUService payUService;

    @GetMapping("")
    public String showAccountForms(Model model, Authentication authentication){
        if(authentication == null){
            model.addAttribute("userDTO", new UserDTO());
            return "account/login-register-form";
        }
        return "redirect:/account/profile";
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        model.addAttribute("user", userDetails.getUser());
        return "account/profile";
    }

    @PostMapping("/profile")
    public String userDataChange(@ModelAttribute("User") User user, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        userService.update(userDetails.getUser(), user);
        return "redirect:/account/profile";
    }

    @GetMapping("/password")
    public String userPasswordChange(Model model){
        model.addAttribute("userPasswordDTO", new UserPasswordDTO());
        return "account/password-form";
    }

    @PostMapping("/password")
    public String processPasswordChange(@Valid @ModelAttribute("userPasswordDTO") UserPasswordDTO userPasswordDTO,
                                        BindingResult bindingResult, Model model, Authentication authentication){
        if (bindingResult.hasErrors())
            return "account/password-form";

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if(userService.passwordMatch(userPasswordDTO.getCurrentPassword(), userDetails.getUser())){
            userService.changePassword(userDetails.getUser(), userPasswordDTO.getNewPassword());
            model.addAttribute("confirmationMessage", "Password was change");
        }
        else
            model.addAttribute("passwordChangeError", "Invalid Current Password");
        return "account/password-form";
    }

    @GetMapping("/verify")
    public String showVerificationForm(){
        return "account/verify-form";
    }

    @PostMapping("/verify")
    public String verifyAccount(@RequestParam("verifyCode") String verifyCode, Model model, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if(userService.verifyUser(userDetails.getUser(), verifyCode))
            model.addAttribute("confirmationMessage", "Your account has been activated");
        else
            model.addAttribute("verifyError", "Incorrect verify code");
        return "account/verify-form";
    }

    @GetMapping("/verify/resend")
    public String resendVerificationCode(RedirectAttributes redirectAttributes, Authentication authentication){
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String verifyCode = userService.generateVerifyCode();
        customUserDetails.getUser().setVerificationCode(verifyCode);
        userService.save(customUserDetails.getUser());
        emailAsyncService.sendVerificationCode(verifyCode, customUserDetails.getEmail());

        redirectAttributes.addFlashAttribute("confirmationMessage", "Verification code has been send again");
        return "redirect:/account/verify";
    }

    @GetMapping("/orders")
    public String showOrderHistory(Model model, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        List<Order> orders = orderService.findByUserId(userDetails.getUser().getId());

        model.addAttribute("orders", orders);
        return "account/orders";
    }

    @GetMapping("/orders/{orderId}")
    public String showOrderDetails(@PathVariable("orderId") int orderId, Model model, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if(!orderService.checkUserOrder(userDetails.getUser(), orderId))
            return "error/404";

        Order order = orderService.findById(orderId);
        List<Book> products = order.getBooks();
        Map<Book, Integer> map = cartService.createProductsMap(products);

        model.addAttribute("order", order);
        model.addAttribute("map", map);
        model.addAttribute("products", products);
        return "account/orderDetails";
    }

    @PostMapping("/orders/{orderId}/received")
    public String orderReceived(@PathVariable("orderId") int orderId, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if(!orderService.checkUserOrder(userDetails.getUser(), orderId))
            return "error/404";

        Order order = orderService.findById(orderId);
        if(order != null) {
            order.setStatus(OrderStatus.COMPLETED.name());
            orderService.save(order);
        }
        else
            return "error/404";

        return "redirect:/account/orders/" + orderId;
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") int orderId, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if(!orderService.checkUserOrder(userDetails.getUser(), orderId))
            return "error/404";

        Order order = orderService.findById(orderId);
        if(order != null){
            payUService.createRefund(order);
            bookService.returnProducts(order.getBooks());
            order.setStatus(OrderStatus.CANCELED.name());
            orderService.save(order);
            emailAsyncService.sendOrderCancel(order);
        }
        else
            return "error/404";

        return "redirect:/account/orders/" + orderId;
    }

    @GetMapping("/password/reset")
    public String showResetPasswordForm(){
        return "account/forgot-password-form";
    }

    @PostMapping("/password/reset")
    public String sendResetPasswordToken(@RequestParam("email") String email, Model model){
        User user = userService.findByEmail(email);
        EmailValidator emailValidator = new EmailValidator();
        if(user == null || !emailValidator.isValid(email)){
            model.addAttribute("errorMessage", "Wrong email");
            return "account/forgot-password-form";
        }
        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);
        emailAsyncService.sendResetPasswordToken(email, token);

        model.addAttribute("message", "We have e-mailed your password reset link!");
        return "account/forgot-password-form";
    }

    @GetMapping("/changePassword")
    public String showChangePasswordForm(@RequestParam(value = "token", required = false) String token, Model model){
        if(token == null)
            return "error/404";

        if(userService.validatePasswordResetToken(token) != null)
            return "error/404";

        model.addAttribute("passwordDTO", new PasswordResetDTO());
        model.addAttribute("token", token);
        return "account/update-password-form";
    }

    @PostMapping("/changePassword")
    public String resetPassword(@Valid @ModelAttribute("passwordDTO") PasswordResetDTO passwordResetDTO, Model model, BindingResult bindingResult){
        if(bindingResult.hasErrors()) {
            model.addAttribute("passwordDTO", new PasswordResetDTO());
            model.addAttribute("token", passwordResetDTO.password);
            return "account/update-password-form";
        }

        if(userService.validatePasswordResetToken(passwordResetDTO.token) != null){
            return "error/404";
        }

        User user = userService.getUserByPasswordResetToken(passwordResetDTO.token);
        if(user != null){
            userService.changePassword(user, passwordResetDTO.password);
            model.addAttribute("message", "Your password has been changed");
            return "account/update-password-form";
        }
        return "error/404";
    }

    @GetMapping("/login-error")
    public String loginError(HttpServletRequest request, Model model){
        HttpSession session = request.getSession(false);
        String errorMessage = null;
        if (session != null){
            AuthenticationException ex = (AuthenticationException) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (ex != null)
                errorMessage = ex.getMessage();
        }

        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("userDTO", new UserDTO());
        return "account/login-register-form";
    }

    @PostMapping("/process_register")
    public String processRegister(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult bindingResult,
                                  Model model, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors())
            return "account/login-register-form";

        if(!userService.checkUniqueUsernameAndEmail(userDTO)){
            model.addAttribute("userDTO", new UserDTO());
            model.addAttribute("registrationError", "Email or Username already exists.");
            return "account/login-register-form";
        }

        String verifyCode = userService.generateVerifyCode();
        userDTO.setVerifyCode(verifyCode);
        userService.save(userDTO);
        User user = userService.findByEmail(userDTO.getEmail());
        Cart cart = Cart.builder()
                .user(user)
                .books(new LinkedList<>())
                .build();
        cartService.save(cart);
        emailAsyncService.sendVerificationCode(verifyCode, user.getEmail());

        redirectAttributes.addFlashAttribute("registerConfirmation", "Success. Log in and activate your account.");
        return "redirect:/account/";
    }
}
