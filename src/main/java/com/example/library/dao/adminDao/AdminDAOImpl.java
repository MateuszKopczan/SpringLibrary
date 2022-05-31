package com.example.library.dao.adminDao;

import com.example.library.entity.User;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class AdminDAOImpl implements AdminDAO{

    @Autowired
    private EntityManager entityManager;

    @Override
    public void save(User user) {
        Session session = entityManager.unwrap(Session.class);
        session.save(user);
    }
}
