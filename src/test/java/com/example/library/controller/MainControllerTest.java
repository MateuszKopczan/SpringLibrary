package com.example.library.controller;

import com.example.library.config.userDetails.CustomUserDetails;
import com.example.library.dto.FilterSortDto;
import com.example.library.entity.Address;
import com.example.library.entity.Book;
import com.example.library.entity.Role;
import com.example.library.entity.User;
import com.example.library.service.bookService.BookService;
import com.example.library.service.categoryService.CategoryService;
import com.example.library.service.orderService.OrderService;
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

import java.util.ArrayList;
import java.util.List;

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
public class MainControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private BookService bookService;

    @MockBean
    private CategoryService categoryService;

    private MockMvc mockMvc;

    private User user;

    private Book book;

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

        book = Book.builder()
                .title("Title")
                .authors(new ArrayList<>())
                .categories(new ArrayList<>())
                .pageCount(123)
                .numberForSale(15)
                .price(24f)
                .imageUrl("url")
                .isbn("11111111111")
                .publicationYear(2000)
                .build();
    }

    @Test
    @WithAnonymousUser
    public void testShowHomePage() throws Exception{
        // given

        // when
        when(bookService.getExclusiveOffer()).thenReturn(new Book());
        when(bookService.findLatest()).thenReturn(new ArrayList<>());
        when(bookService.findFeatured()).thenReturn(new ArrayList<>());

        // then
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("featuredBooks"))
                .andExpect(model().attributeExists("latestBooks"))
                .andExpect(model().attributeExists("exclusiveOffer"));
    }

    @Test
    @WithAnonymousUser
    public void testShowProductsWithoutFilters() throws Exception{
        // given

        // when
        when(bookService.findPage(anyInt(), anyInt(), anyString())).thenReturn(new ArrayList<>());
        when(bookService.getResultCount(anyBoolean(), anyInt(), anyInt(), anyInt(), anyString())).thenReturn(1L);
        when(categoryService.findAll()).thenReturn(new ArrayList<>());

        // then
        mockMvc.perform(get("/products"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attributeExists("latestBooks"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("lastPage"))
                .andExpect(model().attributeExists("filterSortDto"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    @WithAnonymousUser
    public void testShowProductsWithFilters() throws Exception{
        // given
        Integer page = 1;
        String sort = "priceASC";
        String category = "Java";
        Integer downPrice = 20;
        Integer upPrice = 200;

        // when
        when(bookService.findPage(anyInt(), anyInt(), anyString())).thenReturn(new ArrayList<>());
        when(bookService.getResultCount(anyBoolean(), anyInt(), anyInt(), anyInt(), anyString())).thenReturn(1L);
        when(categoryService.findAll()).thenReturn(new ArrayList<>());
        when(bookService.findFiltered(anyInt(), anyInt(), anyInt(), anyInt(), anyString(), anyString())).thenReturn(new ArrayList<>());
        when(bookService.getResultCount(anyBoolean(), anyInt(), anyInt(), anyInt(), anyString())).thenReturn(5L);

        // then
        mockMvc.perform(get("/products")
                .with(csrf())
                .param("page", String.valueOf(page))
                .param("sort", sort)
                .param("category", category)
                .param("downPrice", String.valueOf(downPrice))
                .param("upPrice", String.valueOf(upPrice)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attributeExists("filterSortDto"))
                .andExpect(model().attributeExists("latestBooks"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("lastPage"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    @WithAnonymousUser
    public void testShowSortedProducts() throws Exception{
        // given
        FilterSortDto filterSortDto = FilterSortDto.builder()
                .sort("priceASC")
                .downPrice(60)
                .upPrice(200)
                .categoryName("Java")
                .build();
        // when

        // then
        mockMvc.perform(post("/products")
                        .with(csrf())
                        .flashAttr("filterSortDto", filterSortDto))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeExists("sort"))
                .andExpect(model().attributeExists("downPrice"))
                .andExpect(model().attributeExists("upPrice"))
                .andExpect(model().attributeExists("category"))
                .andExpect(redirectedUrlPattern("/products?**"));
    }

    @Test
    @WithAnonymousUser
    public void testShowProductDetails() throws Exception{
        // given
        int productId = 1;

        // when
        when(bookService.findById(anyInt())).thenReturn(this.book);
        when(bookService.findRelated(anyList())).thenReturn(new ArrayList<>());

        // then
        mockMvc.perform(get("/products/" + productId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("product-details"))
                .andExpect(model().attributeExists("relatedBooks"))
                .andExpect(model().attributeExists("product"));
    }

    @Test
    @WithAnonymousUser
    public void testAccessDenied() throws Exception{
        mockMvc.perform(get("/access-denied"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/404"));
    }

    @Test
    public void testLogout() throws Exception{
        mockMvc.perform(get("/logout")
                .with(user(new CustomUserDetails(this.user))))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
