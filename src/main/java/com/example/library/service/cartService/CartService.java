package com.example.library.service.cartService;

import com.example.library.entity.Book;
import com.example.library.entity.Cart;

import java.util.List;
import java.util.Map;

public interface CartService {
    Cart findByUserId(int id);
    void save(Cart cart);
    void deleteById(int id);
    Map<Book, Integer> createProductsMap(List<Book> products);
    float getCartValue(Cart cart);
}
