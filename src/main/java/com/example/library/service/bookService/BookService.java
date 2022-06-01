package com.example.library.service.bookService;

import com.example.library.dto.BookDTO;
import com.example.library.entity.Book;
import com.example.library.entity.Category;
import com.example.library.payu.OrderCreateRequest;

import java.util.List;

public interface BookService {
    Book getExclusiveOffer();
    List<Book> findAll();
    List<Book> findAllAvailable();
    List<Book> findRelated(List<Category> categories);
    List<Book> findPage(int pageNo, int pageSize, String sortOption);
    Long getResultCount(boolean filtered, int pageSize, Integer downPrice, Integer upPrice, String category);
    Book findById(long id);
    Book findByTitle(String title);

    List<Book> findFiltered(int pageNo, int pageSize, Integer downPrice, Integer upPrice, String categoryName, String sort);
    void save(Book book);
    void save(BookDTO bookDTO);
    void deleteById(long id);
    List<Book> findLatest();
    List<Book> findFeatured();
    void returnProductsFromCanceledOrder(List<OrderCreateRequest.Product> products);
    void returnProducts(List<Book> books);
    List<Book> test(String category);
}
