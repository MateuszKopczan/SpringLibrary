package com.example.library.service;

import com.example.library.service.authorService.AuthorService;
import com.example.library.service.bookService.BookService;
import com.example.library.service.cartService.CartService;
import com.example.library.service.categoryService.CategoryService;
import com.example.library.service.orderService.OrderService;

public interface MainService {

    BookService getBookService();
    AuthorService getAuthorService();
    CategoryService getCategoryService();
    CartService getCartService();
    OrderService getOrderService();
}
