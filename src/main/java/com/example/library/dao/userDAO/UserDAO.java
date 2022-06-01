package com.example.library.dao.userDAO;

import com.example.library.entity.Category;
import com.example.library.entity.User;

import java.util.List;

public interface UserDAO {
    List<User> findAll();
    User findByEmail(String email);
    void save(User user);
    User findByUserName(String userName);
    void deleteById(long id);
}
