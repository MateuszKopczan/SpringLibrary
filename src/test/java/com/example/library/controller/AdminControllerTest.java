package com.example.library.controller;

import com.example.library.config.userDetails.CustomUserDetails;
import com.example.library.dto.BookDTO;
import com.example.library.entity.*;
import com.example.library.payu.OrderRefundResponse;
import com.example.library.service.adminService.AdminService;
import com.example.library.service.bookService.BookService;
import com.example.library.service.cartService.CartService;
import com.example.library.service.emailService.EmailAsyncService;
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

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
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
public class AdminControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private OrderService orderService;

    @MockBean
    private CartService cartService;

    @MockBean
    private BookService bookService;

    @MockBean
    private AdminService adminService;

    @MockBean
    private PayUService payUService;

    @MockBean
    private EmailAsyncService emailAsyncService;

    private MockMvc mockMvc;

    private User user;

    private User admin;

    private Order order;

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

        admin = User.builder()
                .userName("ADMIN")
                .password("PasswordHashed12$%")
                .email("adminemail@gmail.com")
                .address(new Address())
                .firstName("Matt")
                .lastName("Cash")
                .phoneNumber("123-456-245")
                .verify(false)
                .verificationCode("123456")
                .roles(List.of(new Role("ROLE_ADMIN"), new Role("ROLE_USER")))
                .build();

        order = Order.builder()
                .orderDetail(OrderDetails.builder()
                        .firstName("name")
                        .lastName("lastName")
                        .email("email@test.com")
                        .phone("123456789")
                        .address(new Address()).
                        build())
                .user(user)
                .date(new Date())
                .status(OrderStatus.NEW.name())
                .price(123f)
                .books(new LinkedList<>())
                .build();
    }

    @Test
    public void contextLoads(){
        assertThat(bookService).isNotNull();
        assertThat(orderService).isNotNull();
        assertThat(cartService).isNotNull();
        assertThat(adminService).isNotNull();
        assertThat(payUService).isNotNull();
        assertThat(emailAsyncService).isNotNull();
        assertThat(mockMvc).isNotNull();
    }

    @Test
    @WithAnonymousUser
    public void testShowAdminOrdersPageWithAnonymous() throws Exception{
        mockMvc.perform(get("/admin/orders"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testShowAdminOrdersPageWithAuthenticatedUser() throws Exception{
        mockMvc.perform(get("/admin/orders")
                        .with(user(new CustomUserDetails(this.user))))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(forwardedUrl("/access-denied"));
    }

    @Test
    public void testShowAdminOrdersPageWithAuthenticatedAdmin() throws Exception {
        mockMvc.perform(get("/admin/orders")
                        .with(user(new CustomUserDetails(this.admin))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attribute("orders", instanceOf(List.class)));
    }

    @Test
    @WithAnonymousUser
    public void testShowFilteredAdminOrdersPageWithAnonymous() throws Exception{
        mockMvc.perform(post("/admin/orders")
                        .with(csrf())
                        .param("completed", String.valueOf(true))
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testShowFilteredAdminOrdersPageWithAuthenticatedUser() throws Exception{
        mockMvc.perform(post("/admin/orders")
                        .with(user(new CustomUserDetails(this.user)))
                        .with(csrf())
                        .param("completed", String.valueOf(true))
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(forwardedUrl("/access-denied"));
    }

    @Test
    public void testShowFilteredAdminOrdersPageWithAuthenticatedAdmin() throws Exception{
        // given
        String waitingForShipment = String.valueOf(true);

        // when
        when(adminService.findFilteredOrders(anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(new ArrayList<>());

        // then
        mockMvc.perform(post("/admin/orders")
                        .with(user(new CustomUserDetails(this.admin)))
                        .with(csrf())
                        .param("waitingForShipment", waitingForShipment))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attribute("orders", instanceOf(List.class)));
    }

    @Test
    @WithAnonymousUser
    public void testShowOrderDetailsWithAnonymous() throws Exception{
        // given
        int orderId = 1;

        mockMvc.perform(get("/admin/orders/" + orderId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testShowOrderDetailsWithAuthenticatedUser() throws Exception{
        // given
        int orderId = 1;

        mockMvc.perform(get("/admin/orders/" + orderId)
                        .with(user(new CustomUserDetails(this.user)))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(forwardedUrl("/access-denied"));
    }

    @Test
    public void testShowOrderDetailsWithAuthenticatedAdmin() throws Exception{
        // given
        int orderId = 1;
        Order order = this.order;

        // when
        when(orderService.findById(orderId)).thenReturn(order);
        when(cartService.createProductsMap(anyList())).thenReturn(new LinkedHashMap<>());

        // then
        mockMvc.perform(get("/admin/orders/" + orderId)
                        .with(user(new CustomUserDetails(this.admin)))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/orderDetails"))
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attribute("order", instanceOf(Order.class)))
                .andExpect(model().attributeExists("map"))
                .andExpect(model().attribute("map", instanceOf(Map.class)))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("products", instanceOf(List.class)));
    }

    @Test
    @WithAnonymousUser
    public void testMarkingOrderWithAnonymous() throws Exception{
        // given
        int orderId = 1;
        String status = "COMPLETED";

        mockMvc.perform(post("/admin/orders/" + orderId)
                        .with(csrf())
                        .param("status", status))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testMarkingOrderWithAuthenticatedUser() throws Exception{
        // given
        int orderId = 1;
        String status = "COMPLETED";

        mockMvc.perform(post("/admin/orders/" + orderId)
                        .with(user(new CustomUserDetails(this.user)))
                        .with(csrf())
                        .param("status", status))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(forwardedUrl("/access-denied"));
    }

    @Test
    public void testMarkingOrderWithAuthenticatedAdmin() throws Exception{
        // given
        int orderId = 1;
        String status = "SHIPMENT";
        Order order = Order.builder().build();
        OrderRefundResponse orderRefundResponse = OrderRefundResponse.builder().build();

        // when
        when(orderService.findById(orderId)).thenReturn(order);
        when(payUService.createRefund(order)).thenReturn(orderRefundResponse);
        doNothing().when(orderService).save(any(Order.class));
        doNothing().when(emailAsyncService).sendOrderShipment(order);
        doNothing().when(bookService).returnProducts(anyList());
        doNothing().when(emailAsyncService).sendOrderCancel(order);

        // then
        mockMvc.perform(post("/admin/orders/" + orderId)
                        .with(user(new CustomUserDetails(this.admin)))
                        .with(csrf())
                        .param("status", status))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/orders"));
    }

    @Test
    @WithAnonymousUser
    public void testShowAddProductFormWithAnonymous() throws Exception{
        mockMvc.perform(get("/admin/products"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testShowAddProductFormWithAuthenticatedUser() throws Exception{
        mockMvc.perform(get("/admin/products")
                        .with(user(new CustomUserDetails(this.user))))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(forwardedUrl("/access-denied"));
    }

    @Test
    public void testShowAddProductFormWithAuthenticatedAdmin() throws Exception{
        mockMvc.perform(get("/admin/products")
                        .with(user(new CustomUserDetails(this.admin))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-product"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attribute("book", instanceOf(BookDTO.class)));
    }

    @Test
    @WithAnonymousUser
    public void testAddProductWithAnonymous() throws Exception{
        // given
        BookDTO bookDTO = BookDTO.builder().build();

        mockMvc.perform(post("/admin/products")
                        .with(csrf())
                        .flashAttr("book", bookDTO))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testAddProductWithAuthenticatedUser() throws Exception{
        // given
        BookDTO bookDTO = BookDTO.builder().build();

        mockMvc.perform(post("/admin/products")
                        .with(user(new CustomUserDetails(this.user)))
                        .with(csrf())
                        .flashAttr("book", bookDTO))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(forwardedUrl("/access-denied"));
    }

    @Test
    public void testAddProductWithAuthenticatedAdmin() throws Exception{
        // given
        BookDTO bookDTO = BookDTO.builder()
                .title("title")
                .pageCount(123)
                .numberForSale(15)
                .price(50f)
                .isbn("11111111111")
                .categories("Java")
                .build();

        // when
        doNothing().when(bookService).save(any(BookDTO.class));

        //then
        mockMvc.perform(post("/admin/products")
                        .with(user(new CustomUserDetails(this.admin)))
                        .with(csrf())
                        .flashAttr("book", bookDTO))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-product"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", instanceOf(String.class)))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attribute("book", instanceOf(BookDTO.class)));
    }

    @Test
    @WithAnonymousUser
    public void testShowWarehouseWithAnonymous() throws Exception{
        mockMvc.perform(get("/admin/storage"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testShowWarehouseWithAuthenticatedUser() throws Exception{
        mockMvc.perform(get("/admin/storage")
                        .with(user(new CustomUserDetails(this.user))))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(forwardedUrl("/access-denied"));
    }

    @Test
    public void testShowWarehouseWithAuthenticatedAdmin() throws Exception{
        mockMvc.perform(get("/admin/storage")
                        .with(user(new CustomUserDetails(this.admin))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("admin/warehouse"));
    }

    @Test
    @WithAnonymousUser
    public void testShowUpdateProductFormWithAnonymous() throws Exception{
        // given
        int productId = 1;

        mockMvc.perform(get("/admin/storage/" + productId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testShowUpdateProductFormWithAuthenticatedUser() throws Exception{
        // given
        int productId = 1;

        mockMvc.perform(get("/admin/storage/" + productId)
                        .with(user(new CustomUserDetails(this.user)))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(forwardedUrl("/access-denied"));
    }

    @Test
    public void testShowUpdateProductFormWithAuthenticatedAdmin() throws Exception{
        // given
        int productId = 1;
        Book book = Book.builder().build();

        // when
        when(bookService.findById(productId)).thenReturn(book);

        mockMvc.perform(get("/admin/storage/" + productId)
                        .with(user(new CustomUserDetails(this.admin)))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("admin/warehouse"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attribute("book", instanceOf(Book.class)))
                .andExpect(model().attributeExists("crmBook"))
                .andExpect(model().attribute("crmBook", instanceOf(BookDTO.class)));
    }

    @Test
    @WithAnonymousUser
    public void testShowBookFromWarehouseWithAnonymous() throws Exception{
        // given
        int productId = 1;

        mockMvc.perform(post("/admin/storage")
                        .with(csrf())
                        .param("id", String.valueOf(productId)))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testShowBookFromWarehouseWithAuthenticatedUser() throws Exception{
        // given
        int productId = 1;

        mockMvc.perform(post("/admin/storage")
                        .with(user(new CustomUserDetails(this.user)))
                        .with(csrf())
                        .param("id", String.valueOf(productId)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(forwardedUrl("/access-denied"));
    }

    @Test
    public void testShowBookFromWarehouseWithAuthenticatedAdmin() throws Exception{
        // given
        int productId = 1;
        Book book = Book.builder().build();

        // when
        when(bookService.findById(productId)).thenReturn(book);

        // then
        mockMvc.perform(post("/admin/storage")
                        .with(user(new CustomUserDetails(this.admin)))
                        .with(csrf())
                        .param("id", String.valueOf(productId)))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/storage/**"))
                .andExpect(flash().attributeExists("book"))
                .andExpect(flash().attribute("book", instanceOf(Book.class)))
                .andExpect(flash().attributeExists("crmBook"))
                .andExpect(flash().attribute("crmBook", instanceOf(BookDTO.class)));
    }

    @Test
    @WithAnonymousUser
    public void testUpdateBookWithAnonymous() throws Exception{
        // given
        int productId = 1;
        Book book = Book.builder().build();

        mockMvc.perform(post("/admin/storage/" + productId)
                        .with(csrf())
                        .flashAttr("book", book))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testUpdateBookWithAuthenticatedUser() throws Exception{
        // given
        int productId = 1;
        Book book = Book.builder().build();

        mockMvc.perform(post("/admin/storage/" + productId)
                        .with(user(new CustomUserDetails(this.user)))
                        .with(csrf())
                        .flashAttr("book", book))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(forwardedUrl("/access-denied"));
    }

    @Test
    public void testUpdateBookWithAuthenticatedAdmin() throws Exception{
        // given
        int productId = 1;
        Book book = Book.builder().build();

        // when
        when(bookService.findById(anyInt())).thenReturn(book);
        doNothing().when(bookService).save(any(Book.class));

        // then
        mockMvc.perform(post("/admin/storage/" + productId)
                        .with(user(new CustomUserDetails(this.admin)))
                        .with(csrf())
                        .flashAttr("book", book))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("admin/warehouse"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attribute("book", instanceOf(Book.class)))
                .andExpect(model().attributeExists("confirmation"))
                .andExpect(model().attribute("confirmation", instanceOf(String.class)));
    }

    @Test
    @WithAnonymousUser
    public void testRemoveBookWithAnonymous() throws Exception{
        // given
        int productId = 1;

        mockMvc.perform(post("/admin/storage/" + productId + "/remove")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/account"));
    }

    @Test
    public void testRemoveBookWithAuthenticatedUser() throws Exception{
        // given
        int productId = 1;

        mockMvc.perform(post("/admin/storage/" + productId + "/remove")
                        .with(user(new CustomUserDetails(this.user)))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(forwardedUrl("/access-denied"));
    }

    @Test
    public void testRemoveBookWithAuthenticatedAdmin() throws Exception{
        // given
        int productId = 1;

        // when
        doNothing().when(bookService).deleteById(productId);

        // then
        mockMvc.perform(post("/admin/storage/" + productId + "/remove")
                        .with(user(new CustomUserDetails(this.admin)))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/storage"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attribute("message", instanceOf(String.class)));
    }

}
