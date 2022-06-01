package com.example.library.dao.authorDao;

import com.example.library.entity.Author;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuthorDAOImpl implements AuthorDAO{

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Author> findAll() {
        Session session = entityManager.unwrap(Session.class);
        Query<Author> query = session.createQuery("from Author", Author.class);
        List<Author> authors = query.getResultList();
        return authors;
    }

    @Override
    public Author findById(long id) {
        Session session = entityManager.unwrap(Session.class);
        Author author = session.get(Author.class, id);
        return author;
    }

    @Override
    public Author findByName(String name) {
        Session session = entityManager.unwrap(Session.class);
        Query<Author> query = session.createQuery("from Author where name=:uname", Author.class);
        query.setParameter("uname", name);
        try {
            Author author = query.getSingleResult();
            return author;
        } catch (NoResultException e){
            return null;
        }
    }

    @Override
    public void save(Author author) {
        Session session = entityManager.unwrap(Session.class);
        session.saveOrUpdate(author);
    }

    @Override
    public void deleteById(long id) {
        Session session = entityManager.unwrap(Session.class);
        Author author = session.get(Author.class, id);
        session.delete(author);
    }
}
