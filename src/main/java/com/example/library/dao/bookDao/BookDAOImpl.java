package com.example.library.dao.bookDao;

import com.example.library.dao.bookDao.BookDAO;
import com.example.library.entity.Book;
import com.example.library.entity.Category;
import com.sun.mail.util.QEncoderStream;
import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.thymeleaf.expression.Lists;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BookDAOImpl implements BookDAO {
    // TODO : dodać zabezpieczenie przed NULL najlepiej dodać własne klasy wyjątków

    @Autowired
    private EntityManager entityManager;


    @Override
    public Book getExclusiveOffer() {
        Session session = entityManager.unwrap(Session.class);
        Query<Book> query = session.createQuery("from Book order by price desc", Book.class);
        query.setMaxResults(1);
        Book book = query.getSingleResult();
        return book;
    }

    @Override
    public List<Book> findAll() {
        Session session = entityManager.unwrap(Session.class);
        Query<Book> query = session.createQuery("from Book", Book.class);
        List<Book> books = query.getResultList();
        return books;
    }

    @Override
    public List<Book> findAllAvailable() {
        Session session = entityManager.unwrap(Session.class);
        Query<Book> query = session.createQuery("from Book where numberForSale > 0", Book.class);
        List<Book> books = query.getResultList();
        return books;
    }

    @Override
    public List<Book> findRelated(String categoryName) {
        Session session = entityManager.unwrap(Session.class);
        Query<Book> query = session.createQuery("select b from Book b join b.categories c where c.name=:uname", Book.class);
        query.setParameter("uname", categoryName);
        query.setMaxResults(4);
        List<Book> books = query.getResultList();
        return books;
    }

    @Override
    public List<Book> findPage(int pageNo, int pageSize) {
        Session session = entityManager.unwrap(Session.class);
        Query<Book> query = session.createQuery("from Book where numberForSale > 0", Book.class);
        query.setFirstResult(pageNo);
        query.setMaxResults(pageSize);
        List<Book> books = query.getResultList();
        return books;
    }

    @Override
    public List<Book> findPageSortByTitleDESC(int pageNo, int pageSize) {
        Session session = entityManager.unwrap(Session.class);
        Query<Book> query = session.createQuery("from Book where numberForSale > 0 order by title desc", Book.class);
        query.setFirstResult(pageNo);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    @Override
    public List<Book> findPageSortByTitleASC(int pageNo, int pageSize) {
        Session session = entityManager.unwrap(Session.class);
        Query<Book> query = session.createQuery("from Book where numberForSale > 0 order by title asc", Book.class);
        query.setFirstResult(pageNo);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    @Override
    public List<Book> findPageSortByPriceDESC(int pageNo, int pageSize) {
        Session session = entityManager.unwrap(Session.class);
        Query<Book> query = session.createQuery("from Book where numberForSale > 0 order by price desc", Book.class);
        query.setFirstResult(pageNo);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    @Override
    public List<Book> findPageSortByPriceASC(int pageNo, int pageSize) {
        Session session = entityManager.unwrap(Session.class);
        Query<Book> query = session.createQuery("from Book where numberForSale > 0 order by price asc", Book.class);
        query.setFirstResult(pageNo);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    @Override
    public List<Book> findPageInPriceRange(int pageNo, int pageSize, float downPrice, float upPrice) {
        Session session = entityManager.unwrap(Session.class);
        Query<Book> query = session.createQuery("from Book where price >= :udownPrice and price <= :uupPrice", Book.class);
        query.setParameter("udownPrice", downPrice);
        query.setParameter("uupPrice", upPrice);
        query.setFirstResult(pageNo);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    @Override
    public Long getResultCountAfterPriceFilter(int pageSize, float downPrice, float upPrice) {
        Session session = entityManager.unwrap(Session.class);
        Query<Long> query = session.createQuery("SELECT count(*) from Book where price >= :udownPrice and price <= :uupPrice");
        query.setParameter("udownPrice", downPrice);
        query.setParameter("uupPrice", upPrice);
        Long totalCount = query.uniqueResult();
        return totalCount;
    }

    @Override
    public List<Book> findPageInPriceRangeSorted(int pageNo, int pageSize, float downPrice, float upPrice, String sort, String sortOrder) {
        Session session = entityManager.unwrap(Session.class);
        Query<Book> query = session.createQuery("from Book where price >= :udownPrice and price <= :uupPrice order by " + sort + " " + sortOrder, Book.class);
        query.setParameter("udownPrice", downPrice);
        query.setParameter("uupPrice", upPrice);
        query.setFirstResult(pageNo);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    @Override
    public List<Book> findPageInPriceRangeAndCategory(int pageNo, int pageSize, float downPrice, float upPrice, String categoryName) {
        Session session = entityManager.unwrap(Session.class);
        Query<Book> query = session.createQuery("select b from Book b join b.categories c where c.name=:uname and b.price >= :udownPrice and b.price <= :uupPrice");
        query.setParameter("uname", categoryName);
        query.setParameter("udownPrice", downPrice);
        query.setParameter("uupPrice", upPrice);
        query.setFirstResult(pageNo);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    @Override
    public Long getResultCountAfterPriceRangeAndCategoryFilter(int pageSize, float downPrice, float upPrice, String categoryName) {
        Session session = entityManager.unwrap(Session.class);
        Query<Long> query = session.createQuery("SELECT count(*) from Book b join b.categories c where c.name=:uname and b.price >= :udownPrice and b.price <= :uupPrice");
        query.setParameter("udownPrice", downPrice);
        query.setParameter("uupPrice", upPrice);
        query.setParameter("uname", categoryName);
        Long totalCount = query.uniqueResult();
        return totalCount;
    }

    public List<Book> findPageInPriceRangeAndCategorySorted(int pageNo, int pageSize, float downPrice, float upPrice, String categoryName, String sort, String sortOrder) {
        Session session = entityManager.unwrap(Session.class);
        Query<Book> query = session.createQuery("select b from Book b join b.categories c where c.name=:uname and b.price >= :udownPrice and b.price <= :uupPrice order by " + sort + " " + sortOrder);
        query.setParameter("uname", categoryName);
        query.setParameter("udownPrice", downPrice);
        query.setParameter("uupPrice", upPrice);
        query.setFirstResult(pageNo);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    public List<Book> test(){
        Session session = entityManager.unwrap(Session.class);
        String sort = "title";
        Query<Book> query = session.createQuery("select b from Book b join b.categories c where c.name=:ucategory order by " + sort, Book.class);
        query.setParameter("ucategory", "Java");
        return query.getResultList();
    }

    @Override
    public Long getResultCount() {
        Session session = entityManager.unwrap(Session.class);
        Query<Long> query = session.createQuery("SELECT count(*) from Book");
        Long totalCount = query.uniqueResult();
        return totalCount;
    }

    @Override
    public Book findById(int id) {
        Session session = entityManager.unwrap(Session.class);
        Book book = session.get(Book.class, id);
        return book;
    }

    @Override
    public Book findByTitle(String title) {
        Session session = entityManager.unwrap(Session.class);
        Query<Book> query = session.createQuery("from Book where title=:utitle", Book.class);
        query.setParameter("utitle", title);
        Book book = query.getSingleResult();
        return book;
    }

    @Override
    public void save(Book book) {
        Session session = entityManager.unwrap(Session.class);
        session.saveOrUpdate(book);
    }

    @Override
    public void deleteById(int id) {
        Session session = entityManager.unwrap(Session.class);
        Book book = session.get(Book.class, id);
        session.delete(book);
    }

    @Override
    public List<Book> findLatest() {
        Session session = entityManager.unwrap(Session.class);
        Query<Book> query = session.createQuery("from Book order by publication_year DESC", Book.class).setMaxResults(8);
        List<Book> latestBooks = query.getResultList();
        return latestBooks;
    }

    @Override
    public List<Book> findFeatured() {
        Session session = entityManager.unwrap(Session.class);
        Query<Book> query = session.createQuery("from Book", Book.class).setMaxResults(4);
        List<Book> featuredBooks = query.getResultList();
        return featuredBooks;
    }
}
