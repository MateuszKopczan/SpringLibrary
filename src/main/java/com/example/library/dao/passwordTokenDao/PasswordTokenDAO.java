package com.example.library.dao.passwordTokenDao;

import com.example.library.entity.PasswordResetToken;
import com.example.library.entity.User;

public interface PasswordTokenDAO {

    void save(PasswordResetToken passwordResetToken);
    PasswordResetToken findByToken(String token);
    User getUserByPasswordResetToken(String token);
}
