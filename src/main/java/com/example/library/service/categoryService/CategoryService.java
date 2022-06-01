package com.example.library.service.categoryService;

import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.example.library.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> findAll();
    Category findById(long id);
    Category findByName(String name);
    void save(Category category);
    void deleteById(long id);
}
