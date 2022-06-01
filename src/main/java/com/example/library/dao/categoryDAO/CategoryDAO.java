package com.example.library.dao.categoryDAO;

import com.example.library.entity.Category;

import java.util.List;

public interface CategoryDAO {

    List<Category> findAll();
    Category findById(long id);
    Category findByName(String name);
    void save(Category category);
    void deleteById(long id);
}
