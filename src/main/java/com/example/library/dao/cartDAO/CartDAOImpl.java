package com.example.library.dao.cartDAO;

import com.example.library.entity.Cart;
import com.example.library.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.swing.text.html.parser.Entity;

@Repository
public class CartDAOImpl implements CartDAO{

    @Autowired
    private EntityManager entityManager;

    @Override
    public Cart findByUserId(long id) {
        Session session = entityManager.unwrap(Session.class);
        Query<Cart> query = session.createQuery("from Cart where user_id=:uid", Cart.class);
        query.setParameter("uid", id);
        try {
            Cart cart = query.getSingleResult();
            return cart;
        } catch (NoResultException e){
            return null;
        }
    }

    @Override
    public void save(Cart cart) {
        Session session = entityManager.unwrap(Session.class);
        session.saveOrUpdate(cart);
    }

    @Override
    public void deleteById(long id) {
        Session session = entityManager.unwrap(Session.class);
        Cart cart = session.get(Cart.class, id);
        session.delete(cart);
    }
}
