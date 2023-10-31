package com.ecommerce_apis.infrastructure.repositories;

import com.ecommerce_apis.domain.entities.Role;
import com.ecommerce_apis.infrastructure.repositories.RoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setName("ROLE_TEST");
        role.setDescription("This is role test");
        roleRepository.save(role);
    }

    @AfterEach
    void tearDown() {
        Role deleteRole = roleRepository.findByName("ROLE_TEST");
        if(deleteRole != null) {
            roleRepository.delete(deleteRole);
        }
    }

    @Test
    void findByName() {
        Role findRoleName = roleRepository.findByName("ROLE_TEST");
        String roleName = "ROLE_TEST";
        assertEquals(roleName, findRoleName.getName());
    }
}