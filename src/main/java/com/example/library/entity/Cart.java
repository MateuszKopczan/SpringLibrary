package com.example.library.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user", "books"})
@Builder
@Entity
@Table(name="cart")
public class Cart extends BaseEntity{

    @OneToOne(fetch= FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="user_id")
    private User user;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE,
                            CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(
            name="cart_book",
            joinColumns = @JoinColumn(name="cart_id"),
            inverseJoinColumns = @JoinColumn(name="book_id")
    )
    private List<Book> books;

    public void addBookToCart(Book book){
        if (books == null)
            books = new ArrayList<>();
        books.add(book);
    }

    public void removeBookFromCart(long id){
        for(Book book : books){
            if(book.getId() == id) {
                books.remove(book);
                break;
            }
        }
    }
    public void removeAllBooksFromCartOfGivenId(long id){
        books.removeIf(e -> e.getId() == id);
    }
}
