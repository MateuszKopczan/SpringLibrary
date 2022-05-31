package com.example.library.service;

import com.example.library.service.authorService.AuthorService;
import com.example.library.service.bookService.BookService;
import com.example.library.service.cartService.CartService;
import com.example.library.service.categoryService.CategoryService;
import com.example.library.service.orderService.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MainServiceImpl implements MainService{

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Override
    public BookService getBookService() {
        return bookService;
    }

    @Override
    public AuthorService getAuthorService() {
        return authorService;
    }

    @Override
    public CategoryService getCategoryService() {
        return categoryService;
    }

    @Override
    public CartService getCartService() {
        return cartService;
    }

    @Override
    public OrderService getOrderService() {
        return orderService;
    }
}
