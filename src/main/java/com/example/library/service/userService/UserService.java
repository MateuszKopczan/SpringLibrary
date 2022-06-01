package com.example.library.service.userService;

import com.example.library.dto.UserCheckoutDTO;
import com.example.library.dto.UserDTO;
import com.example.library.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    List<User> findAll();
    User findByEmail(String email);
    User findByUserName(String userName);
    void save(UserDTO userDTO);
    void save(User user);
    void changePassword(User user, String password);
    void update(User sessionUser, User userData);
    boolean verifyUser(User user, String verifyCode);
    UserCheckoutDTO copyValue(User user);
    void deleteById(long id);
    void createPasswordResetTokenForUser(User user, String token);
    String validatePasswordResetToken(String token);
    User getUserByPasswordResetToken(String token);
    String generateVerifyCode();
    boolean checkUniqueUsernameAndEmail(UserDTO userDTO);
    boolean passwordMatch(String password, User user);
}
