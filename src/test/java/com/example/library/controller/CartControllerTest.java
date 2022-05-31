package com.example.library.controller;

import com.example.library.config.userDetails.CustomUserDetails;
import com.example.library.entity.*;
import com.example.library.service.bookService.BookService;
import com.example.library.service.cartService.CartService;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
public class CartControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private CartService cartService;

    @MockBean
    private BookService bookService;

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
    public void testShowCartWithAnonymous() throws Exception{
        mockMvc.perform(get("/cart"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testShowCartWithAuthenticatedUser() throws Exception{
        // given
        Cart cart = Cart.builder().books(new LinkedList<>()).build();

        // when
        when(cartService.findByUserId(anyInt())).thenReturn(cart);
        when(cartService.createProductsMap(anyList())).thenReturn(new HashMap<>());
        doNothing().when(cartService).save(any(Cart.class));

        // then
        mockMvc.perform(get("/cart")
                        .with(user(new CustomUserDetails(this.user))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/cart"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("products", instanceOf(List.class)))
                .andExpect(model().attributeExists("map"))
                .andExpect(model().attribute("map", instanceOf(Map.class)));
    }

    @Test
    @WithAnonymousUser
    public void testAddProductToCartWithAnonymous() throws Exception{
        // given
        Book book = Book.builder().build();

        // then
        mockMvc.perform(post("/cart/add")
                        .with(csrf())
                        .flashAttr("Book", book))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testAddProductToCartWithAuthenticatedUser() throws Exception{
        // given
        Book book = Book.builder().build();

        // when
        when(cartService.findByUserId(anyInt())).thenReturn(new Cart());
        when(bookService.findById(anyInt())).thenReturn(book);
        doNothing().when(cartService).save(any(Cart.class));

        // then
        mockMvc.perform(post("/cart/add")
                        .with(user(new CustomUserDetails(this.user)))
                        .with(csrf())
                        .flashAttr("Book", book))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }

    @Test
    @WithAnonymousUser
    public void testRemoveProductFromCartWithAnonymous() throws Exception{
        // given
        Book book = Book.builder().build();

        // then
        mockMvc.perform(post("/cart/remove")
                        .with(csrf())
                        .flashAttr("Book", book))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testRemoveProductFromCartWithAuthenticatedUser() throws Exception{
        // given
        int productId = 1;
        Cart cart = Cart.builder().books(new LinkedList<>()).build();

        // when
        when(cartService.findByUserId(anyInt())).thenReturn(cart);
        doNothing().when(cartService).save(any(Cart.class));
//        doNothing().when(cart).removeAllBooksFromCartOfGivenId(anyInt());

        // then
        mockMvc.perform(get("/cart/remove")
                        .with(user(new CustomUserDetails(this.user)))
                        .with(csrf())
                        .param("productId", String.valueOf(productId)))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }

    @Test
    @WithAnonymousUser
    public void testDecreaseProductQuantityInCartWithAnonymous() throws Exception{
        // given
        Book book = Book.builder().build();

        // then
        mockMvc.perform(post("/cart/update/decrease")
                        .with(csrf())
                        .flashAttr("Book", book))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testDecreaseProductQuantityInCartWithAuthenticatedUser() throws Exception{
        // given
        Book book = Book.builder().build();
        Cart cart = Cart.builder().books(new LinkedList<>()).build();

        // when
        when(cartService.findByUserId(anyInt())).thenReturn(cart);
        doNothing().when(cartService).save(any(Cart.class));

        // then
        mockMvc.perform(post("/cart/update/decrease")
                        .with(user(new CustomUserDetails(this.user)))
                        .with(csrf())
                        .flashAttr("Book", book))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }

    @Test
    @WithAnonymousUser
    public void testIncreaseProductQuantityInCartWithAnonymous() throws Exception{
        // given
        Book book = Book.builder().build();

        // then
        mockMvc.perform(post("/cart/update/increase")
                        .with(csrf())
                        .flashAttr("Book", book))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testIncreaseProductQuantityInCartWithAuthenticatedUser() throws Exception{
        // given
        Book book = Book.builder().build();
        Cart cart = Cart.builder().books(new LinkedList<>()).build();

        // when
        when(cartService.findByUserId(anyInt())).thenReturn(cart);
        when(bookService.findById(anyInt())).thenReturn(book);
        doNothing().when(cartService).save(any(Cart.class));

        // then
        mockMvc.perform(post("/cart/update/increase")
                        .with(user(new CustomUserDetails(this.user)))
                        .with(csrf())
                        .flashAttr("Book", book))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }
}
