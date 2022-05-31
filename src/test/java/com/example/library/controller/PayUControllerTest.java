package com.example.library.controller;

import com.example.library.config.userDetails.CustomUserDetails;
import com.example.library.entity.Address;
import com.example.library.entity.Order;
import com.example.library.entity.Role;
import com.example.library.entity.User;
import com.example.library.payu.OrderCreateResponse;
import com.example.library.service.orderService.OrderService;
import com.example.library.service.payuService.PayUService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
@SpringBootTest
public class PayUControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private OrderService orderService;

    @MockBean
    private PayUService payUService;

    private MockMvc mockMvc;

    private User user;

    @Before
    public void setup() {
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
    public void testProcessPaymentWithAnonymous() throws Exception{
        mockMvc.perform(get("/checkout/process"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testProcessPaymentWithAuthenticatedUser() throws Exception{
        // given
        Order order = Order.builder().user(this.user).build();
        Map map = new HashMap();
        OrderCreateResponse orderCreateResponse = OrderCreateResponse.builder()
                .status(OrderCreateResponse.PayUResponseStatus.builder()
                        .statusCode("SUCCESS")
                        .build())
                .build();

        // when
        when(payUService.createNewOrder(any(Order.class), anyMap(), anyString())).thenReturn(orderCreateResponse);
        doNothing().when(orderService).save(any(Order.class));

        // then
        mockMvc.perform(get("/checkout/process")
                        .with(csrf())
                        .with(user(new CustomUserDetails(this.user)))
                        .flashAttr("order", order)
                        .flashAttr("map", map))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testCallbackPayment() throws Exception{
        // given

        // when
        when(orderService.findLatestByUserId(anyInt())).thenReturn(new Order());

        // then
        mockMvc.perform(get("/checkout/callback")
                .with(user(new CustomUserDetails(this.user))))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/account/orders/**"));
    }
}
