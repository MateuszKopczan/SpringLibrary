package com.example.library.service.categoryService;

import com.example.library.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.library.dao.categoryDAO.CategoryDAO;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryDAO categoryDAO;

    @Override
    public List<Category> findAll() {
        return categoryDAO.findAll();
    }

    @Override
    public Category findById(long id) {
        return categoryDAO.findById(id);
    }

    @Override
    public Category findByName(String name) {
        return categoryDAO.findByName(name);
    }

    @Override
    public void save(Category category) {
        categoryDAO.save(category);
    }

    @Override
    public void deleteById(long id) {
        categoryDAO.deleteById(id);
    }
}
