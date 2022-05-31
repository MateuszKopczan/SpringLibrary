package com.example.library.dao.authorDao;

import com.example.library.entity.Author;

import java.util.List;

public interface AuthorDAO {

    List<Author> findAll();
    Author findById(int id);
    Author findByName(String name);
    void save(Author author);
    void deleteById(int id);
}
