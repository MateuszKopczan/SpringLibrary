package com.example.library.controller;


import com.example.library.config.userDetails.CustomUserDetails;
import com.example.library.dto.PasswordResetDTO;
import com.example.library.dto.UserDTO;
import com.example.library.dto.UserPasswordDTO;
import com.example.library.entity.*;
import com.example.library.payu.OrderRefundResponse;
import com.example.library.service.bookService.BookService;
import com.example.library.service.cartService.CartService;
import com.example.library.service.emailService.EmailAsyncService;
import com.example.library.service.orderService.OrderService;
import com.example.library.service.payuService.PayUService;
import com.example.library.service.userService.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;


import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
@SpringBootTest
public class AccountControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private BookService bookService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private CartService cartService;

    @MockBean
    private UserService userService;

    @MockBean
    private PayUService payUService;

    @MockBean
    private EmailAsyncService emailAsyncService;

    private MockMvc mockMvc;

    private User correctUser;

    private UserDTO userDTO;

    private Order order;

    private ObjectMapper objectMapper;

    @Before
    public void setup(){
        mockMvc = MockMvcBuilders
                    .webAppContextSetup(context)
                    .apply(springSecurity())
                    .build();

        correctUser = User.builder()
                    .userName("User")
                    .password("Password123!#")
                    .email("correctemaiL@gmail.com")
                    .address(new Address())
                    .firstName("John")
                    .lastName("Wick")
                    .phoneNumber("123-456-245")
                    .verify(false)
                    .verificationCode("123456")
                    .roles(List.of(new Role("ROLE_USER")))
                    .build();

        order = Order.builder()
                .orderDetail(OrderDetails.builder()
                        .firstName("name")
                        .lastName("lastName")
                        .email("email@test.com")
                        .phone("123456789")
                        .address(new Address()).
                        build())
                .user(correctUser)
                .date(new Date())
                .status(OrderStatus.NEW.name())
                .price(123f)
                .books(new LinkedList<>())
                .build();

        userDTO = new UserDTO("User", "Password123!#", "Password123!#", "correctemaiL@gmail.com", "123456");

        objectMapper = new ObjectMapper();
    }

    @Test
    public void contextLoads(){
        assertThat(bookService).isNotNull();
        assertThat(orderService).isNotNull();
        assertThat(cartService).isNotNull();
        assertThat(mockMvc).isNotNull();
    }

    @Test
    @WithAnonymousUser
    public void testShowAccountFormsWithoutUserAuthentication() throws Exception{
        mockMvc.perform(get("/account"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/login-register-form"))
                .andExpect(model().attribute("userDTO", instanceOf(UserDTO.class)));
    }

    @Test
    @WithMockUser
    public void testRedirectUserWithAuthenticationToAccountProfilePage() throws Exception{
        mockMvc.perform(get("/account"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/profile"));
    }

    @Test
    @WithAnonymousUser
    public void testShowProfilePageWithAnonymousUser() throws Exception{
        mockMvc.perform(get("/account/profile"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testShowProfilePageWithAuthenticatedUser() throws Exception{
        mockMvc.perform(get("/account/profile")
                        .with(user(new CustomUserDetails(this.correctUser))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("user", instanceOf(User.class)))
                .andExpect(model().attribute("user", correctUser))
                .andExpect(view().name("account/profile"));
    }

    @Test
    public void testUserDataChangeWithAuthenticatedUser() throws Exception{
        doNothing().when(userService).update(correctUser, correctUser);

        mockMvc.perform(post("/account/profile")
                        .with(user(new CustomUserDetails(this.correctUser)))
                        .with(csrf())
                        .flashAttr("User", correctUser))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/profile"));
    }

    @Test
    @WithAnonymousUser
    public void testUserDataChangeWithAnonymousUser() throws Exception{
        mockMvc.perform(post("/account/profile")
                    .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    @WithAnonymousUser
    public void testShowPasswordChangeFormWithAnonymousUser() throws Exception{
        mockMvc.perform(get("/account/password"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testShowPasswordChangeFormWithAuthenticatedUser() throws Exception{
        mockMvc.perform(get("/account/password")
                        .with(user(new CustomUserDetails(this.correctUser))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/password-form"))
                .andExpect(model().attribute("userPasswordDTO", instanceOf(UserPasswordDTO.class)));
    }

    @Test
    @WithAnonymousUser
    public void testUserPasswordChangeWithAnonymousUser() throws Exception{
        mockMvc.perform(post("/account/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserPasswordDTO())))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testUserPasswordChangeWithAuthenticatedUserAndCorrectData() throws Exception{
        // given
        UserPasswordDTO userPasswordDTO = new UserPasswordDTO(correctUser.getPassword(), "CorrectPassword789#", "CorrectPassword789#");
        given(userService.passwordMatch(any(String.class), any(User.class))).willReturn(true);

        // when
        doNothing().when(userService).changePassword(any(User.class), any(String.class));

        // then
        mockMvc.perform(post("/account/password")
                        .with(user(new CustomUserDetails(this.correctUser)))
                        .with(csrf())
                        .flashAttr("userPasswordDTO", userPasswordDTO))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/password-form"))
                .andExpect(model().attribute("confirmationMessage", "Password was change"));

    }

    @Test
    public void testUserPasswordChangeWithAuthenticatedUserAndIncorrectData() throws  Exception{
        // given
        UserPasswordDTO userPasswordDTO = new UserPasswordDTO(correctUser.getPassword(), "incorrect", "incorrect");
        given(userService.passwordMatch(any(String.class), any(User.class))).willReturn(true);

        // when
        doNothing().when(userService).changePassword(any(User.class), any(String.class));

        // then
        mockMvc.perform(post("/account/password")
                        .with(user(new CustomUserDetails(this.correctUser)))
                        .with(csrf())
                        .flashAttr("userPasswordDTO", userPasswordDTO))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/password-form"))
                .andExpect(model().attributeHasErrors("userPasswordDTO"));
    }

    @Test
    public void testUserPasswordChangeWithAuthenticatedUserAndIncorrectNewPassword() throws Exception{
        // given
        UserPasswordDTO userPasswordDTO = new UserPasswordDTO("incorrect", "Password1!", "Password1!");
        given(userService.passwordMatch(any(String.class), any(User.class))).willReturn(false);

        // when
        doNothing().when(userService).changePassword(any(User.class), any(String.class));

        // then
        mockMvc.perform(post("/account/password")
                        .with(user(new CustomUserDetails(this.correctUser)))
                        .with(csrf())
                        .flashAttr("userPasswordDTO", userPasswordDTO))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/password-form"))
                .andExpect(model().attribute("passwordChangeError", "Invalid Current Password"));
    }

    @Test
    @WithAnonymousUser
    public void testShowVerificationFormWithAnonymousUser() throws Exception{
        mockMvc.perform(get("/account/verify"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testShowVerificationFormWithAuthenticatedUser() throws Exception{
        mockMvc.perform(get("/account/verify")
                        .with(user(new CustomUserDetails(this.correctUser))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/verify-form"));
    }

    @Test
    @WithAnonymousUser
    public void testVerifyAccountWithAnonymousUser() throws Exception{
        mockMvc.perform(post("/account/verify")
                        .with(csrf())
                        .param("verifyCode", "123456"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testVerifyAccountWithAuthenticatedUserAndCorrectVerifyCode() throws Exception{
        // given
        String verifyCode = "123456";
        given(userService.verifyUser(any(User.class), any(String.class))).willReturn(true);

        mockMvc.perform(post("/account/verify")
                        .with(user(new CustomUserDetails(this.correctUser)))
                        .with(csrf())
                        .param("verifyCode", verifyCode))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/verify-form"))
                .andExpect(model().attributeExists("confirmationMessage"));
    }

    @Test
    public void testVerifyAccountWithAuthenticatedUserAndIncorrectVerifyCode() throws Exception{
        // given
        String verifyCode = "654321";
        given(userService.verifyUser(any(User.class), any(String.class))).willReturn(false);

        mockMvc.perform(post("/account/verify")
                        .with(user(new CustomUserDetails(this.correctUser)))
                        .with(csrf())
                        .param("verifyCode", verifyCode))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/verify-form"))
                .andExpect(model().attributeExists("verifyError"));
    }

    @Test
    @WithAnonymousUser
    public void testResendVerificationCodeWithAnonymousUser() throws Exception{
        mockMvc.perform(get("/account/verify/resend"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testResendVerificationCodeWithAuthenticatedUser() throws Exception{
        doNothing().when(userService).save(any(User.class));
        when(emailAsyncService.sendVerificationCode(any(String.class), any(String.class))).thenReturn("456126");

        mockMvc.perform(get("/account/verify/resend")
                        .with(user(new CustomUserDetails(this.correctUser))))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/verify"))
                .andExpect(flash().attributeExists("confirmationMessage"));
    }

    @Test
    @WithAnonymousUser
    public void testShowOrderHistoryWithAnonymousUser() throws Exception{
        mockMvc.perform(get("/account/orders"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testShowOrderHistoryWithAuthenticatedUser() throws Exception{
        when(orderService.findByUserId(any(Integer.class))).thenReturn(new LinkedList<>());

        mockMvc.perform(get("/account/orders")
                        .with(user(new CustomUserDetails(this.correctUser))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/orders"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attribute("orders", instanceOf(List.class)));
    }

    @Test
    @WithAnonymousUser
    public void testShowOrderDetailsWithAnonymousUser() throws Exception{
        int orderId = 1;

        mockMvc.perform(get("/account/orders/" + orderId))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testShowOrderDetailsWithAuthenticatedUserAndCorrectOrderId() throws Exception{
        // given
        int orderId = 1;

        // when
        when(orderService.checkUserOrder(correctUser, orderId)).thenReturn(true);
        when(orderService.findById(orderId)).thenReturn(this.order);
        when(cartService.createProductsMap(new LinkedList<>())).thenReturn(new LinkedHashMap<>());

        // then
        mockMvc.perform(get("/account/orders/" + orderId)
                        .with(user(new CustomUserDetails(this.correctUser))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/orderDetails"))
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attributeExists("map"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("order", instanceOf(Order.class)))
                .andExpect(model().attribute("map", instanceOf(Map.class)))
                .andExpect(model().attribute("products", instanceOf(List.class)));

    }

    @Test
    public void testShowOrderDetailsWithAuthenticatedUserAndInCorrectOrderId() throws Exception{
        // given
        int orderId = 2;

        // when
        when(orderService.checkUserOrder(correctUser, orderId)).thenReturn(false);

        // then
        mockMvc.perform(get("/account/orders/" + orderId)
                        .with(user(new CustomUserDetails(this.correctUser))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("error/404"));
    }

    @Test
    public void testMarkOrderAsReceivedWithAuthenticatedUserAndCorrectOrderId() throws Exception{
        // given
        int orderId = 1;

        // when
        when(orderService.checkUserOrder(any(User.class), any(Integer.class))).thenReturn(true);
        when(orderService.findById(orderId)).thenReturn(this.order);
        doNothing().when(orderService).save(any(Order.class));

        // then
        mockMvc.perform(post("/account/orders/" + orderId + "/received")
                        .with(csrf())
                        .with(user(new CustomUserDetails(this.correctUser))))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/orders/" + orderId));
    }

    @Test
    public void testMarkOrderAsReceivedWithAuthenticatedUserAndIncorrectOrderId() throws Exception{
        // given
        int orderId = 2;

        // when
        when(orderService.checkUserOrder(this.correctUser, orderId)).thenReturn(false);
        when(orderService.findById(any(Integer.class))).thenReturn(null);
        doNothing().when(orderService).save(any(Order.class));

        // then
        mockMvc.perform(post("/account/orders/" + orderId + "/received")
                        .with(csrf())
                        .with(user(new CustomUserDetails(this.correctUser))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("error/404"));
    }

    @Test
    public void testMarkOrderAsCanceledWithAuthenticatedUserAndCorrectOrderId() throws Exception{
        // given
        int orderId = 1;

        // when
        when(orderService.checkUserOrder(any(User.class), any(Integer.class))).thenReturn(true);
        when(orderService.findById(1)).thenReturn(this.order);
        when(payUService.createRefund(this.order)).thenReturn(OrderRefundResponse.builder().build());
        doNothing().when(orderService).save(this.order);
        doNothing().when(emailAsyncService).sendOrderCancel(this.order);

        // then
        mockMvc.perform(post("/account/orders/" + orderId + "/cancel")
                        .with(csrf())
                        .with(user(new CustomUserDetails(this.correctUser))))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/orders/" + orderId));
    }

    @Test
    public void testMarkOrderAsCancelWithAuthenticatedUserAndIncorrectOrderId() throws Exception{
        // given
        int orderId = 2;

        // when
        when(orderService.checkUserOrder(any(User.class), any(Integer.class))).thenReturn(false);
        when(orderService.findById(any(Integer.class))).thenReturn(null);

        // then
        mockMvc.perform(post("/account/orders/" + orderId + "/received")
                        .with(csrf())
                        .with(user(new CustomUserDetails(this.correctUser))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("error/404"));
    }

    @Test
    @WithAnonymousUser
    public void testShowResetPasswordFormWithAnonymousUser() throws Exception{
        mockMvc.perform(get("/account/password/reset"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/forgot-password-form"));
    }

    @Test
    @WithAnonymousUser
    public void testSendResetPasswordTokenWithAnonymousUserAndCorrectEmail() throws Exception{
        // given
        String email = "exampleEmail@test.com";
        String token = UUID.randomUUID().toString();

        // when
        when(userService.findByEmail(email)).thenReturn(correctUser);
        doNothing().when(userService).createPasswordResetTokenForUser(correctUser, token);
        doNothing().when(emailAsyncService).sendResetPasswordToken(email, token);

        // then
        mockMvc.perform(post("/account/password/reset")
                        .with(csrf())
                        .param("email", email))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/forgot-password-form"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    @WithAnonymousUser
    public void testSendResetPasswordTokenWithAnonymousUserAndInCorrectEmail() throws Exception{
        // given
        String email = "example.Email@test";

        // when
        when(userService.findByEmail(email)).thenReturn(correctUser);

        // then
        mockMvc.perform(post("/account/password/reset")
                        .with(csrf())
                        .param("email", email))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/forgot-password-form"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    @WithAnonymousUser
    public void testShowChangePasswordFormWithAnonymousUserAndCorrectToken() throws Exception{
        // given
        String token = "correctToken";

        // when
        when(userService.validatePasswordResetToken(token)).thenReturn(any(String.class));

        // then
        mockMvc.perform(get("/account/changePassword")
                        .with(csrf())
                        .param("token", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/update-password-form"))
                .andExpect(model().attributeExists("passwordDTO"))
                .andExpect(model().attributeExists("token"))
                .andExpect(model().attribute("passwordDTO", instanceOf(PasswordResetDTO.class)))
                .andExpect(model().attribute("token", token));
    }

    @Test
    @WithAnonymousUser
    public void testShowChangePasswordFormWithAnonymousUserAndIncorrectToken() throws Exception{
        // given
        String token = "incorrectToken";

        // when
        when(userService.validatePasswordResetToken(token)).thenReturn(anyString());

        // then
        mockMvc.perform(get("/account/changePassword")
                        .with(csrf())
                        .param("token", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("error/404"));
    }

    @Test
    @WithAnonymousUser
    public void testResetPasswordWithAnonymousUserAndCorrectData() throws Exception{
        // given
        String token = "correctToken";
        PasswordResetDTO passwordResetDTO = new PasswordResetDTO("Password1!", "Password1!", token);

        // when
        when(userService.validatePasswordResetToken(token)).thenReturn(any(String.class));
        when(userService.getUserByPasswordResetToken(token)).thenReturn(correctUser);
        doNothing().when(userService).changePassword(correctUser, passwordResetDTO.getPassword());

        // then
        mockMvc.perform(post("/account/changePassword")
                        .with(csrf())
                        .flashAttr("passwordDTO", passwordResetDTO))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/update-password-form"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    @WithAnonymousUser
    public void testResetPasswordWithAnonymousUserAndInCorrectData() throws Exception{
        // given
        String token = "incorrectToken";
        PasswordResetDTO passwordResetDTO = new PasswordResetDTO("Password1!", "Password1!", token);

        // when
        when(userService.validatePasswordResetToken(token)).thenReturn(anyString());
        when(userService.getUserByPasswordResetToken(token)).thenReturn(correctUser);
        doNothing().when(userService).changePassword(correctUser, passwordResetDTO.getPassword());

        // then
        mockMvc.perform(post("/account/changePassword")
                        .with(csrf())
                        .flashAttr("passwordDTO", passwordResetDTO))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("error/404"));
    }

    @Test
    @WithAnonymousUser
    public void testCorrectProcessRegister() throws Exception{
        // when
        when(userService.checkUniqueUsernameAndEmail(this.userDTO)).thenReturn(true);
        when(userService.generateVerifyCode()).thenReturn("123456");
        when(emailAsyncService.sendVerificationCode("123456", this.userDTO.getEmail())).thenReturn("123456");
        when(userService.findByEmail(this.userDTO.getEmail())).thenReturn(this.correctUser);
        doNothing().when(userService).save(this.userDTO);

        // then
        mockMvc.perform(post("/account/process_register")
                        .with(csrf())
                        .flashAttr("userDTO", this.userDTO))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/"))
                .andExpect(flash().attributeExists("registerConfirmation"));
    }

    @Test
    @WithAnonymousUser
    public void testProcessRegisterWithNotUniqueEmail() throws Exception{
        // when
        when(userService.checkUniqueUsernameAndEmail(this.userDTO)).thenReturn(false);

        // then
        mockMvc.perform(post("/account/process_register")
                        .with(csrf())
                        .flashAttr("userDTO", this.userDTO))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/login-register-form"))
                .andExpect(model().attributeExists("userDTO"))
                .andExpect(model().attributeExists("registrationError"));
    }

    @Test
    @WithAnonymousUser
    public void testProcessRegisterWithIncorrectData() throws Exception{
        mockMvc.perform(post("/account/process_register")
                        .with(csrf())
                        .flashAttr("userDTO", new UserDTO()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/login-register-form"))
                .andExpect(model().attributeHasErrors("userDTO"));
    }

}
