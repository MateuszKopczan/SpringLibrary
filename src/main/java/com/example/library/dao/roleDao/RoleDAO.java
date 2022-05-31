package com.example.library.dao.roleDao;

import com.example.library.entity.Role;

public interface RoleDAO {

    Role findRoleByName(String theRoleName);
}
