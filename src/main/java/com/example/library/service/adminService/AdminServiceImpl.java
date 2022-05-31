package com.example.library.service.adminService;

import com.example.library.dao.adminDao.AdminDAO;
import com.example.library.dao.orderDAO.OrderDAO;
import com.example.library.dao.roleDao.RoleDAO;
import com.example.library.dao.userDAO.UserDAO;
import com.example.library.dto.UserDTO;
import com.example.library.entity.Order;
import com.example.library.entity.Role;
import com.example.library.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminServiceImpl implements AdminService{

    @Autowired
    private AdminDAO adminDAO;

    @Autowired
    private OrderDAO orderDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Override
    public void save(UserDTO userDTO) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        User user = new User();
        user.setUserName(userDTO.getUserName());
        user.setPassword(encodedPassword);
        user.setEmail(userDTO.getEmail());
        user.setRoles(List.of(roleDAO.findRoleByName("ROLE_ADMIN")));
        adminDAO.save(user);
    }

    @Override
    public List<Order> findFilteredOrders(boolean completed, boolean canceled, boolean waitingForShipment, boolean shipment) {
        if(!completed && !canceled && !waitingForShipment && !shipment)
            return orderDAO.findAll();

        List<Order> allOrders = new LinkedList<>();
        if(completed)
            allOrders.addAll(orderDAO.findCompletedOrders());
        if(canceled)
            allOrders.addAll(orderDAO.findCanceledOrders());
        if(waitingForShipment)
            allOrders.addAll(orderDAO.findWaitingForShipment());
        if(shipment)
            allOrders.addAll(orderDAO.findShipment());

        allOrders.sort(Comparator.comparing(Order::getId));
        return allOrders;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userDAO.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }
}
