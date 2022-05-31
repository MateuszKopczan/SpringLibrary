package com.example.library.dao.passwordTokenDao;

import com.example.library.entity.PasswordResetToken;
import com.example.library.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@Repository
public class PasswordTokenDAOImpl implements PasswordTokenDAO{

    @Autowired
    private EntityManager entityManager;

    @Override
    public void save(PasswordResetToken passwordResetToken) {
        Session session = entityManager.unwrap(Session.class);
        session.save(passwordResetToken);
    }

    @Override
    public PasswordResetToken findByToken(String token) {
        Session session = entityManager.unwrap(Session.class);
        Query<PasswordResetToken> query = session.createQuery("from PasswordResetToken where token=:utoken", PasswordResetToken.class);
        query.setParameter("utoken", token);
        try {
            return query.getSingleResult();
        } catch (NoResultException e){
            return null;
        }
    }

    @Override
    public User getUserByPasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = findByToken(token);
        if(passwordResetToken != null)
            return passwordResetToken.getUser();
        return null;
    }
}
