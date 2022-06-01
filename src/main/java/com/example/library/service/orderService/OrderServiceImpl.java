package com.example.library.service.orderService;

import com.example.library.dao.orderDAO.OrderDAO;
import com.example.library.dto.UserCheckoutDTO;
import com.example.library.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDAO orderDAO;

    @Override
    public List<Order> findAll() {
        List<Order> orders = orderDAO.findAll();
        return orders;
    }

    @Override
    public void save(Order order) {
        orderDAO.save(order);
    }

    @Override
    public Order findById(long id) {
        Order order = orderDAO.findById(id);
        return order;
    }

    @Override
    public Order findLatestByUserId(long userId) {
        Order order = orderDAO.findLatestByUserId(userId);
        return order;
    }

    @Override
    public List<Order> findByUserId(long userId) {
        List<Order> userOrders = orderDAO.findByUserId(userId);
        return userOrders;
    }

    @Override
    public boolean checkUserOrder(User user, long id) {
        Order order = orderDAO.findById(id);
        if(order == null)
            return false;
        return order.getUser().getId() == user.getId();
    }

    @Override
    public Order findByPayUId(String payUId) {
        Order order = orderDAO.findByPayUId(payUId);
        return order;
    }

    @Override
    public Order createOrder(UserCheckoutDTO userCheckoutDTO, User user, float cartValue, List<Book> products) {
        Order order = Order.builder()
                .user(user)
                .price(cartValue + 8)
                .books(products)
                .address(userCheckoutDTO.getAddress())
                .date(new Date())
                .status(OrderStatus.NEW.name())
                .orderDetail(OrderDetails.builder()
                        .email(userCheckoutDTO.getEmail())
                        .firstName(userCheckoutDTO.getFirstName())
                        .lastName(userCheckoutDTO.getLastName())
                        .phone(userCheckoutDTO.getPhoneNumber())
                        .address(userCheckoutDTO.getAddress())
                        .build())
                .build();
        orderDAO.save(order);
        return order;
    }
}
