package com.example.library.dao.bookDao;

import com.example.library.entity.Book;
import com.example.library.entity.Category;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookDAO {

    Book getExclusiveOffer();
    List<Book> findAll();
    List<Book> findAllAvailable();
    List<Book> findRelated(String categoryName);
    List<Book> findPage(int pageNo, int pageSize);
    List<Book> findPageSortByTitleDESC(int pageNo, int pageSize);
    List<Book> findPageSortByTitleASC(int pageNo, int pageSize);
    List<Book> findPageSortByPriceDESC(int pageNo, int pageSize);
    List<Book> findPageSortByPriceASC(int pageNo, int pageSize);
    List<Book> findPageInPriceRange(int pageNo, int pageSize, float downPrice, float upPrice);
    Long getResultCountAfterPriceFilter(int pageSize, float downPrice, float upPrice);
    List<Book> findPageInPriceRangeSorted(int pageNo, int pageSize, float downPrice, float upPrice, String sort, String sortOrder);
    List<Book> findPageInPriceRangeAndCategory(int pageNo, int pageSize, float downPrice, float upPrice, String categoryName);
    Long getResultCountAfterPriceRangeAndCategoryFilter(int pageSize, float downPrice, float upPrice, String categoryName);
    List<Book> findPageInPriceRangeAndCategorySorted(int pageNo, int pageSize, float downPrice, float upPrice, String categoryName, String sort, String sortOrder);
    Long getResultCount();
    Book findById(long id);
    Book findByTitle(String title);
    void save(Book book);
    void deleteById(long id);
    List<Book> findLatest();
    List<Book> findFeatured();



    List<Book> test();
}
