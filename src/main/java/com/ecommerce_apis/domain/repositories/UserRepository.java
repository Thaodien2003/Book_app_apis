package com.ecommerce_apis.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce_apis.domain.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	User findByEmail(String email);
	
}