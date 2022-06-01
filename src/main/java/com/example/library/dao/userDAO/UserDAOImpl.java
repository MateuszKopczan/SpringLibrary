package com.example.library.dao.userDAO;

import com.example.library.entity.Cart;
import com.example.library.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

@Repository
public class UserDAOImpl implements UserDAO{

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<User> findAll() {
        Session session = entityManager.unwrap(Session.class);
        Query<User> query = session.createQuery("from User", User.class);
        List<User> users = query.getResultList();
        return users;
    }

    @Override
    public User findByEmail(String email) {
        Session session = entityManager.unwrap(Session.class);
        Query<User> query = session.createQuery("from User where email=:uemail", User.class);
        query.setParameter("uemail", email);
        try {
            User user = query.getSingleResult();
            return user;
        } catch (NoResultException e){
            return null;
        }
    }

    @Override
    public void save(User user) {
        Session session = entityManager.unwrap(Session.class);
        session.saveOrUpdate(user);
    }

    @Override
    public User findByUserName(String userName) {
        Session session = entityManager.unwrap(Session.class);
        Query<User> query = session.createQuery("from User where username=:uusername", User.class);
        query.setParameter("uusername", userName);
        try {
            User user = query.getSingleResult();
            return user;
        } catch (NoResultException e){
            return null;
        }
    }

    @Override
    public void deleteById(long id) {
        Session session = entityManager.unwrap(Session.class);
        User user = session.get(User.class, id);
        session.delete(user);
    }
}
