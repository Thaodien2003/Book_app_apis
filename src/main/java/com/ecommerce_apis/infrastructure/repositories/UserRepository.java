package com.ecommerce_apis.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecommerce_apis.domain.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	User findByEmail(String email);
}
