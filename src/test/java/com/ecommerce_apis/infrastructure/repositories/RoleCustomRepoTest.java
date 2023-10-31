package com.ecommerce_apis.infrastructure.repositories;

import com.ecommerce_apis.domain.entities.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RoleCustomRepoTest {

    @Autowired
    private  RoleCustomRepo roleCustomRepo;

    @Test
    void getRole() {
        String email = "vu123@gmail.com";
        List<Role> roles = roleCustomRepo.getRole(email);
        assertNotNull(roles);
        assertFalse(roles.isEmpty());
    }
}