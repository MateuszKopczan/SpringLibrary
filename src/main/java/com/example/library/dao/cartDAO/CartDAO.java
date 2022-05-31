package com.example.library.dao.cartDAO;

import com.example.library.entity.Cart;
import com.example.library.entity.Category;

import java.util.List;

public interface CartDAO {
    Cart findByUserId(int id);
    void save(Cart cart);
    void deleteById(int id);
}
