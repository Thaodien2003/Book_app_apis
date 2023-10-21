package com.ecommerce_apis.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce_apis.domain.entities.Role;


@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByName(String role);
}
