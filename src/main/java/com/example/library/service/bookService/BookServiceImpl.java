package com.example.library.service.bookService;

import com.example.library.dao.authorDao.AuthorDAO;
import com.example.library.dao.bookDao.BookDAO;
import com.example.library.dao.categoryDAO.CategoryDAO;
import com.example.library.dto.BookDTO;
import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.example.library.entity.Category;
import com.example.library.payu.OrderCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    @Autowired
    private BookDAO bookDAO;

    @Autowired
    private AuthorDAO authorDAO;

    @Autowired
    private CategoryDAO categoryDAO;

    @Override
    public Book getExclusiveOffer() {
        return bookDAO.getExclusiveOffer();
    }

    @Override
    public List<Book> findAll() {
        return bookDAO.findAll();
    }

    @Override
    public List<Book> findAllAvailable() {
        return bookDAO.findAllAvailable();
    }

    @Override
    public List<Book> findRelated(List<Category> categories) {
        List<Book> relatedBooks = new ArrayList<>();
        for(Category category : categories){
            relatedBooks.addAll(bookDAO.findRelated(category.getName()));
        }
        if(relatedBooks.size() > 4)
            return relatedBooks.subList(0, 4);
        return relatedBooks;
    }

    @Override
    public List<Book> findPage(int pageNo, int pageSize, String sortOption) {
        if(sortOption.equals("titleASC"))
            return bookDAO.findPageSortByTitleASC(pageNo, pageSize);
        else if(sortOption.equals("titleDESC"))
            return bookDAO.findPageSortByTitleDESC(pageNo, pageSize);
        else if(sortOption.equals("priceASC"))
            return bookDAO.findPageSortByPriceASC(pageNo, pageSize);
        else if(sortOption.equals("priceDESC"))
            return bookDAO.findPageSortByPriceDESC(pageNo, pageSize);
        else
            return bookDAO.findPage(pageNo, pageSize);
    }

    @Override
    public Long getResultCount(boolean filtered, int pageSize, Integer downPrice, Integer upPrice, String category) {
        if(downPrice == null)
            downPrice = 0;
        if(upPrice == null)
            upPrice = 100000;

        if(filtered){
            if(!category.equals(""))
                return bookDAO.getResultCountAfterPriceRangeAndCategoryFilter(pageSize, (float)downPrice, (float)upPrice, category);
            else
                return bookDAO.getResultCountAfterPriceFilter(pageSize, (float)downPrice, (float)upPrice);
        }
        else
            return bookDAO.getResultCount();
    }

    @Override
    public Book findById(int id) {
        Optional<Book> result = Optional.ofNullable(bookDAO.findById(id));
        if(result.isPresent())
            return result.get();
        else
            return null;
            //throw new RuntimeException("Did not find Book id: " + id);
    }

    @Override
    public Book findByTitle(String title) {
        return bookDAO.findByTitle(title);
    }

    @Override
    public List<Book> findFiltered(int pageNo, int pageSize, Integer downPrice, Integer upPrice, String categoryName, String sort) {
        if(downPrice == null)
            downPrice = 0;
        if(upPrice == null)
            upPrice = 100000;
        if(sort.equals("default"))
            sort = null;

        System.out.println(upPrice);
        System.out.println(downPrice);
        System.out.println(categoryName);
        System.out.println(sort);

        if(sort != null) {
            if(categoryName.equals(""))
                return bookDAO.findPageInPriceRangeSorted(pageNo, pageSize, (float) downPrice, upPrice, sort.substring(0,5), sort.substring(5));
            else
                return bookDAO.findPageInPriceRangeAndCategorySorted(pageNo, pageSize, (float) downPrice, (float) upPrice, categoryName, sort.substring(0,5), sort.substring(5));
        }
        else{
            if(categoryName.equals(""))
                return bookDAO.findPageInPriceRange(pageNo, pageSize, (float) downPrice, (float) upPrice);
            else
                return bookDAO.findPageInPriceRangeAndCategory(pageNo, pageSize, (float) downPrice, (float) upPrice, categoryName);
        }
    }


    @Override
    public void save(Book book) {
        bookDAO.save(book);
    }

    @Override
    public void save(BookDTO bookDTO) {
        Book book = Book.builder()
                .title(bookDTO.getTitle())
                .authors(getAuthors(bookDTO.getAuthors()))
                .categories(getCategories(bookDTO.getCategories()))
                .pageCount(bookDTO.getPageCount())
                .numberForSale(bookDTO.getNumberForSale())
                .price(bookDTO.getPrice())
                .imageUrl(bookDTO.getImageUrl())
                .isbn(bookDTO.getIsbn())
                .publicationYear(bookDTO.getPublicationYear())
                .shortDescription(bookDTO.getShortDescription())
                .longDescription(bookDTO.getLongDescription())
                .build();
        bookDAO.save(book);
    }

    @Override
    public void deleteById(int id) {
        bookDAO.deleteById(id);
    }

    @Override
    public List<Book> findLatest() {
        return bookDAO.findLatest();
    }

    @Override
    public List<Book> findFeatured() {
        return bookDAO.findFeatured();
    }

    @Override
    public void returnProductsFromCanceledOrder(List<OrderCreateRequest.Product> products) {
        for(OrderCreateRequest.Product product : products){
            Book book = bookDAO.findByTitle(product.getName());
            int numberForSale = book.getNumberForSale() + Integer.parseInt(product.getQuantity());
            book.setNumberForSale(numberForSale);
            bookDAO.save(book);
        }
    }

    @Override
    public void returnProducts(List<Book> books) {
        for(Book book : books){
            book.setNumberForSale(book.getNumberForSale() + 1);
            bookDAO.save(book);
        }
    }

    @Override
    public List<Book> test(String category) {
        return bookDAO.test();//new ArrayList<>();//bookDAO.findPageInCategory(1, 200, category);
    }

    private List<Author> getAuthors(String authorsString){
        String[] authorsTable = authorsString.split(",");
        List<Author> authors = new LinkedList<>();
        for(String authorName : authorsTable){
            authorName = authorName.trim();
            Author dbAuthor = authorDAO.findByName(authorName);
            if(dbAuthor != null)
                authors.add(dbAuthor);
            else{
                Author author = new Author();
                author.setName(authorName);
                authors.add(author);
                authorDAO.save(author);
            }
        }
        return authors;
    }

    private List<Category> getCategories(String categoriesString){
        String[] categoriesTable = categoriesString.split(",");
        List<Category> categories = new LinkedList<>();
        for(String categoryName : categoriesTable){
            categoryName = categoryName.trim();
            Category dbCategory = categoryDAO.findByName(categoryName);
            if(dbCategory != null)
                categories.add(dbCategory);
            else{
                Category category = new Category();
                category.setName(categoryName);
                categories.add(category);
                categoryDAO.save(category);
            }
        }
        return categories;
    }
}
