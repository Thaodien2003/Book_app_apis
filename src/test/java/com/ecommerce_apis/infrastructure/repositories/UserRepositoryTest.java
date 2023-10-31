package com.ecommerce_apis.infrastructure.repositories;

import com.ecommerce_apis.domain.entities.User;
import com.ecommerce_apis.infrastructure.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user1 = new User();
        user1.setUsername("TestUser1");
        user1.setPassword("12345");
        user1.setEmail("user1@gmail.com");
        userRepository.save(user1);
    }

    @AfterEach
    void tearDown() {
        User userToDelete = userRepository.findByEmail("user1@gmail.com");
        if (userToDelete != null) {
            userRepository.delete(userToDelete);
        }
    }

    @Test
    void findByEmail() {
        User findEmailUser = userRepository.findByEmail("user1@gmail.com");
        String expectedEmail = "user1@gmail.com";
        assertEquals(expectedEmail, findEmailUser.getEmail());
    }
}
