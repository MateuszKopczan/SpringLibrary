package com.example.library.dao.orderDAO;

import com.example.library.entity.Order;
import com.example.library.entity.User;

import java.util.Collection;
import java.util.List;

public interface OrderDAO {
    List<Order> findAll();
    void save(Order order);
    Order findById(long id);
    List<Order> findByUserId(long userId);
    Order findLatestByUserId(long userId);
    Order findByPayUId(String payUId);
    List<Order> findCompletedOrders();
    List<Order> findCanceledOrders();
    List<Order> findWaitingForShipment();
    List<Order> findShipment();
}
