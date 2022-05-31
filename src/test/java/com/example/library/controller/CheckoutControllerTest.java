package com.example.library.controller;

import com.example.library.config.userDetails.CustomUserDetails;
import com.example.library.dto.UserCheckoutDTO;
import com.example.library.entity.*;
import com.example.library.service.cartService.CartService;
import com.example.library.service.emailService.EmailAsyncService;
import com.example.library.service.orderService.OrderService;
import com.example.library.service.userService.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
@SpringBootTest
public class CheckoutControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private CartService cartService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private UserService userService;

    @MockBean
    private EmailAsyncService emailAsyncService;

    private MockMvc mockMvc;

    private User user;

    @Before
    public void setup(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        user = User.builder()
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
    }

    @Test
    @WithAnonymousUser
    public void testListCheckoutWithAnonymous() throws Exception{
        mockMvc.perform(get("/checkout"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testListCheckoutWithAuthenticatedUser() throws Exception{
        // given
        Cart cart = Cart.builder().books(new LinkedList<>()).build();

        // when
        when(cartService.findByUserId(anyInt())).thenReturn(cart);
        when(userService.copyValue(any(User.class))).thenReturn(new UserCheckoutDTO());
        when(cartService.createProductsMap(anyList())).thenReturn(new HashMap<>());

        // then
        mockMvc.perform(get("/checkout")
                        .with(user(new CustomUserDetails(this.user))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/checkout"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("products", instanceOf(List.class)))
                .andExpect(model().attributeExists("map"))
                .andExpect(model().attribute("map", instanceOf(Map.class)))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", instanceOf(User.class)))
                .andExpect(model().attributeExists("crmUserCheckout"))
                .andExpect(model().attribute("crmUserCheckout", instanceOf(UserCheckoutDTO.class)));
    }

    @Test
    @WithAnonymousUser
    public void testPrepareCheckoutWithAnonymous() throws Exception{
        mockMvc.perform(post("/checkout"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(forwardedUrl("/access-denied"));
    }

    @Test
    public void testPrepareCheckoutWithAuthenticatedUser() throws Exception{
        // given
        UserCheckoutDTO userCheckoutDTO = UserCheckoutDTO.builder()
                .firstName("John")
                .lastName("Wick")
                .phoneNumber("123456789")
                .email("example@gmail.com")
                .build();
        Cart cart = Cart.builder().books(new ArrayList<>()).build();

        // when
        when(cartService.findByUserId(anyInt())).thenReturn(cart);
        when(cartService.getCartValue(cart)).thenReturn(0f);
        when(cartService.createProductsMap(anyList())).thenReturn(new HashMap<>());
        when(orderService.createOrder(any(UserCheckoutDTO.class), any(User.class), anyFloat(), anyList())).thenReturn(new Order());
        doNothing().when(userService).save(any(User.class));
        doNothing().when(cartService).deleteById(anyInt());
        doNothing().when(emailAsyncService).sendOrderConfirmation(any(Order.class));

        // then
        mockMvc.perform(post("/checkout")
                        .with(user(new CustomUserDetails(this.user)))
                        .with(csrf())
                        .flashAttr("crmUserCheckout", userCheckoutDTO))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("order"))
                .andExpect(flash().attribute("order", instanceOf(Order.class)))
                .andExpect(flash().attributeExists("map"))
                .andExpect(flash().attribute("map", instanceOf(Map.class)))
                .andExpect(redirectedUrl("/checkout/process"));

    }
}
