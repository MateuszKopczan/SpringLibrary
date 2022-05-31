package com.example.library.entity;

import lombok.*;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@ToString(exclude = {"books"})
@Builder
@Entity
@Table(name="author")
public class Author extends BaseEntity{

    @Column(name="name", unique = true)
    private String name;

    @ManyToMany(fetch=FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.DETACH,
                                                CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name="books_authors",
            joinColumns = @JoinColumn(name="author_id"),
            inverseJoinColumns = @JoinColumn(name="book_id")
    )
    private List<Book> books;

    public void addBook(Book book){
        if(books == null)
            books = new ArrayList<>();
        if(book != null)
            books.add(book);
    }

    @Override
    public String toString() {
        return name;
    }
}
