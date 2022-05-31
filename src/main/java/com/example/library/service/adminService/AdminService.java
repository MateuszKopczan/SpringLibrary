package com.example.library.service.adminService;

import com.example.library.dto.UserDTO;
import com.example.library.entity.Order;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface AdminService extends UserDetailsService  {

    void save(UserDTO userDTO);
    List<Order> findFilteredOrders(boolean completed, boolean canceled, boolean waitingForShipment, boolean shipment);
}
