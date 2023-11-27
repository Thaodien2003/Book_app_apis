package com.book_app_apis.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.book_app_apis.domain.entities.Role;


@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByName(String role);
}
