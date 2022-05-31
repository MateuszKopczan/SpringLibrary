package com.example.library.service.orderService;

import com.example.library.dto.UserCheckoutDTO;
import com.example.library.entity.Book;
import com.example.library.entity.Order;
import com.example.library.entity.User;

import java.util.List;

public interface OrderService {
    List<Order> findAll();
    void save(Order order);
    Order findById(int id);
    Order findLatestByUserId(int userId);
    List<Order> findByUserId(int userId);
    boolean checkUserOrder(User user, int id);
    Order findByPayUId(String payUId);
    Order createOrder(UserCheckoutDTO userCheckoutDTO, User user, float cartValue, List<Book> products);
}
