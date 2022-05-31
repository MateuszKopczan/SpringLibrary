package com.example.library.service.authorService;

import com.example.library.entity.Author;

import java.util.List;

public interface AuthorService {

    List<Author> findAll();
    Author findById(int id);
    Author findByName(String name);
    void save(Author book);
    void deleteById(int id);
}
