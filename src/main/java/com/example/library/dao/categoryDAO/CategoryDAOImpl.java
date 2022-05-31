package com.example.library.dao.categoryDAO;

import com.example.library.entity.Category;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

@Repository
public class CategoryDAOImpl implements CategoryDAO {

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Category> findAll() {
        Session session = entityManager.unwrap(Session.class);
        Query<Category> query = session.createQuery("from Category ", Category.class);
        List<Category> categories = query.getResultList();
        return categories;
    }

    @Override
    public Category findById(int id) {
        Session session = entityManager.unwrap(Session.class);
        Category category = session.get(Category.class, id);
        return category;
    }

    @Override
    public Category findByName(String name) {
        Session session = entityManager.unwrap(Session.class);
        Query<Category> query = session.createQuery("from Category where name=:uname", Category.class);
        query.setParameter("uname", name);
        try{
            Category category = query.getSingleResult();
            return category;
        }catch (NoResultException e){
            return null;
        }
    }

    @Override
    public void save(Category category) {
        Session session = entityManager.unwrap(Session.class);
        session.saveOrUpdate(category);
    }

    @Override
    public void deleteById(int id) {
        Session session = entityManager.unwrap(Session.class);
        Category category = session.get(Category.class, id);
        session.delete(category);
    }
}
