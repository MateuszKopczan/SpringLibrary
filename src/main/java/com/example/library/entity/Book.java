package com.example.library.entity;

import lombok.*;

import javax.persistence.*;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"authors"})
@Builder
@Entity
@Table(name="book")
public class Book extends BaseEntity{

    @Column(name="title")
    private String title;

    @Column(name="page_count")
    private int pageCount;

    @Column(name="number_to_borrow")
    private int numberToBorrow;

    @Column(name="number_for_sale")
    private int numberForSale;

    @Column(name="price")
    private float price;

    @Column(name="image_url")
    private String imageUrl;

    @Column(name="isbn")
    private String isbn;

    @Column(name="publication_year")
    private int publicationYear;

    @Column(name="short_description")
    private String shortDescription;

    @Column(name="long_description")
    private String longDescription;

    @ManyToMany(fetch=FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.DETACH,
                                                CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name="books_authors",
            joinColumns = @JoinColumn(name="book_id"),
            inverseJoinColumns = @JoinColumn(name="author_id")
    )
    private List<Author> authors;

    @ManyToMany(fetch=FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.DETACH,
                                                CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
        name="books_categories",
        joinColumns = @JoinColumn(name="book_id"),
        inverseJoinColumns = @JoinColumn(name="category_id")
    )
    private List<Category> categories;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="course_id")
    private List<Review> reviews;

//    public Book(){}

    public void addAuthor(Author author){
        if (authors == null)
            authors = new ArrayList<>();
        if (author != null)
            authors.add(author);
    }

    public void addCategory(Category category){
        if (categories == null)
            categories = new ArrayList<>();
        if (category != null)
            categories.add(category);
    }

    public void addReview(Review review){
        if (reviews == null)
            reviews = new ArrayList<>();
        if (review != null)
            reviews.add(review);
    }

}


