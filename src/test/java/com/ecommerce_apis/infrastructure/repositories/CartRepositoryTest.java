package com.ecommerce_apis.infrastructure.repositories;

import com.ecommerce_apis.domain.entities.Cart;
import com.ecommerce_apis.domain.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    String user_id;
    Long cart_id;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("Test");
        user.setEmail("test@gmail.com");
        userRepository.save(user);
        user_id = user.getUser_id();

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setTotalPrice(100000);
        cartRepository.save(cart);
        cart_id = cart.getId();
    }

    @AfterEach
    void tearDown() {
        cartRepository.deleteById(cart_id);
        userRepository.deleteById(user_id);
    }

    @SuppressWarnings("AssertBetweenInconvertibleTypes")
    @Test
    void findByUserId() {
        Cart carts = cartRepository.findByUserId(user_id);
        assertNotNull(carts);
        assertEquals(user_id, carts.getUser().getUser_id());
    }
}