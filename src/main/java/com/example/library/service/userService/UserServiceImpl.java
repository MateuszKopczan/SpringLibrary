package com.example.library.service.userService;

import com.example.library.dao.passwordTokenDao.PasswordTokenDAO;
import com.example.library.dao.roleDao.RoleDAO;
import com.example.library.dao.userDAO.UserDAO;
import com.example.library.dto.UserCheckoutDTO;
import com.example.library.dto.UserDTO;
import com.example.library.entity.PasswordResetToken;
import com.example.library.entity.Role;
import com.example.library.entity.User;
import com.example.library.config.userDetails.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService{

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private PasswordTokenDAO passwordTokenDAO;

    @Override
    public List<User> findAll() {
        return userDAO.findAll();
    }

    @Override
    public User findByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    @Override
    public User findByUserName(String userName) {
        return userDAO.findByUserName(userName);
    }

    @Override
    public void save(UserDTO userDTO) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        User user = new User();
        user.setUserName(userDTO.getUserName());
        user.setPassword(encodedPassword);
        user.setEmail(userDTO.getEmail());
        user.setCart(new Cart());
        user.setVerificationCode(userDTO.getVerifyCode());
        user.setRoles(List.of(roleDAO.findRoleByName("ROLE_USER")));
        userDAO.save(user);
    }

    @Override
    public void save(User user) {
        userDAO.save(user);
    }

    @Override
    public void changePassword(User user, String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
        userDAO.save(user);
    }

    @Override
    public void update(User sessionUser, User userData) {
        sessionUser.setFirstName(userData.getFirstName());
        sessionUser.setLastName(userData.getLastName());
        sessionUser.setPhoneNumber(userData.getPhoneNumber());
        sessionUser.setAddress(userData.getAddress());
        userDAO.save(sessionUser);
//        User user = userDAO.findByEmail(userData.getEmail());
//        System.out.println(user);
//        if(user != null){
//            user.setFirstName(userData.getFirstName());
//            user.setLastName(userData.getLastName());
//            user.setPhoneNumber(userData.getPhoneNumber());
//            user.setAddress(userData.getAddress());
//            userDAO.save(user);
//        }
    }

    @Override
    public boolean verifyUser(User user, String verifyCode) {
        if(user.getVerificationCode().equals(verifyCode)){
            user.setVerify(true);
            userDAO.save(user);
            return true;
        }
        return false;
    }

    @Override
    public UserCheckoutDTO copyValue(User user) {
        UserCheckoutDTO userCheckoutDTO = UserCheckoutDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .address(user.getAddress())
                .build();
        return userCheckoutDTO;
    }

    @Override
    public void deleteById(long id) {
        userDAO.deleteById(id);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken userToken = new PasswordResetToken(user, token);
        passwordTokenDAO.save(userToken);
    }

    @Override
    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passwordResetToken = passwordTokenDAO.findByToken(token);
        return !isTokenFound(passwordResetToken) ? "Invalid token"
                : isTokenExpired(passwordResetToken) ? "Token expired"
                : null;
    }

    @Override
    public User getUserByPasswordResetToken(String token) {
        return passwordTokenDAO.getUserByPasswordResetToken(token);
    }

    @Override
    public String generateVerifyCode() {
        return String.valueOf(new Random().nextInt(899999) + 100000);
    }

    @Override
    public boolean checkUniqueUsernameAndEmail(UserDTO userDTO) {
        User userWithGivenEmail = userDAO.findByEmail(userDTO.getEmail());
        User userWithGivenUserName = userDAO.findByUserName(userDTO.getUserName());
        return userWithGivenEmail == null && userWithGivenUserName == null;
    }

    @Override
    public boolean passwordMatch(String password, User user) {
        return new BCryptPasswordEncoder().matches(password, user.getPassword());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userDAO.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new CustomUserDetails(user);
//        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
//                mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

    private boolean isTokenFound(PasswordResetToken passwordResetToken){
        return passwordResetToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passwordResetToken){
        final Calendar calendar = Calendar.getInstance();
        return passwordResetToken.getExpiryDate().before(calendar.getTime());
    }
}
