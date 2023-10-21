package com.ecommerce_apis.domain.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecommerce_apis.domain.entities.Role;
import com.ecommerce_apis.domain.entities.User;


@Repository
public interface RoleCustomRepo extends JpaRepository<User, String> {
    @Query(value = "SELECT r.name as name "
            + "FROM users u "
            + "JOIN user_role ur ON u.user_id=ur.user_id "
            + "JOIN roles r ON r.id = ur.role_id "
            + "WHERE (:email is null or u.email=:email)", nativeQuery = true)
    List<Role> getRole(@Param("email") String email);
}