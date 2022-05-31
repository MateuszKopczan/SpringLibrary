package com.example.library.dao.orderDAO;

import com.example.library.entity.Book;
import com.example.library.entity.Order;
import com.example.library.entity.OrderStatus;
import com.example.library.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class OrderDAOImpl implements OrderDAO{

    @Autowired
    private EntityManager entityManager;


    @Override
    public List<Order> findAll() {
        Session session = entityManager.unwrap(Session.class);
        Query<Order> query = session.createQuery("from Order", Order.class);
        List<Order> orders = query.getResultList();
        return orders;
    }

    @Override
    public void save(Order order) {
        Session session = entityManager.unwrap(Session.class);
        session.saveOrUpdate(order);
    }

    @Override
    public Order findById(int id) {
        Session session = entityManager.unwrap(Session.class);
        Order order = session.get(Order.class, id);
        return order;
    }

    @Override
    public List<Order> findByUserId(int userId) {
        Session session = entityManager.unwrap(Session.class);
        Query<Order> query = session.createQuery("from Order where user_id=:uid", Order.class);
        query.setParameter("uid", userId);
        List<Order> userOrders = query.getResultList();
        return userOrders;
    }

    @Override
    public Order findLatestByUserId(int userId) {
        Session session = entityManager.unwrap(Session.class);
        Query<Order> query = session.createQuery("from Order where user_id=:uid order by date desc", Order.class);
        query.setParameter("uid", userId);
        query.setMaxResults(1);
        Order order = query.getSingleResult();
        return order;
    }

    @Override
    public Order findByPayUId(String payUId) {
        Session session = entityManager.unwrap(Session.class);
        Query<Order> query = session.createQuery("from Order where payu_order_id=:uid", Order.class);
        query.setParameter("uid", payUId);
        Order order = query.getSingleResult();
        return order;
    }


    @Override
    public List<Order> findCompletedOrders() {
        Session session = entityManager.unwrap(Session.class);
        Query<Order> query = session.createQuery("from Order where order_status=:ustatus", Order.class);
        query.setParameter("ustatus", OrderStatus.COMPLETED.name());
        List<Order> orders = query.getResultList();
        return orders;
    }

    @Override
    public List<Order> findCanceledOrders() {
        Session session = entityManager.unwrap(Session.class);
        Query<Order> query = session.createQuery("from Order where order_status=:ustatus", Order.class);
        query.setParameter("ustatus", OrderStatus.CANCELED.name());
        List<Order> orders = query.getResultList();
        return orders;
    }

    @Override
    public List<Order> findWaitingForShipment() {
        Session session = entityManager.unwrap(Session.class);
        Query<Order> query = session.createQuery("from Order where order_status=:ustatus", Order.class);
        query.setParameter("ustatus", OrderStatus.WAITING_FOR_SHIPMENT.name());
        List<Order> orders = query.getResultList();
        return orders;
    }

    @Override
    public List<Order> findShipment() {
        Session session = entityManager.unwrap(Session.class);
        System.out.println("DUPA");
        Query<Order> query = session.createQuery("from Order where order_status=:ustatus", Order.class);
        query.setParameter("ustatus", OrderStatus.SHIPMENT.name());
        List<Order> orders = query.getResultList();
        System.out.println(orders);
        return orders;
    }
}
