package com.example.library.dao.roleDao;

import com.example.library.entity.Role;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class RoleDAOImpl implements RoleDAO {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Role findRoleByName(String theRoleName) {
        Session session = entityManager.unwrap(Session.class);

        Query<Role> query = session.createQuery("from Role where name=:roleName", Role.class);
        query.setParameter("roleName", theRoleName);

        Role theRole = null;

        try {
            theRole = query.getSingleResult();
        } catch (Exception e) {
            theRole = null;
        }
        return theRole;
    }
}
