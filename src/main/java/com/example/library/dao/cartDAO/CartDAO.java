package com.example.library.dao.cartDAO;

import com.example.library.entity.Cart;
import com.example.library.entity.Category;

import java.util.List;

public interface CartDAO {
    Cart findByUserId(long id);
    void save(Cart cart);
    void deleteById(long id);
}
