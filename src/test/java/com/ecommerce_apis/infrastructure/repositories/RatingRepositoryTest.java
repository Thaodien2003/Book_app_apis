package com.ecommerce_apis.infrastructure.repositories;

import com.ecommerce_apis.domain.entities.Product;
import com.ecommerce_apis.domain.entities.Rating;
import com.ecommerce_apis.domain.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RatingRepositoryTest {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    Long productId;
    String user_id;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setUsername("Test");
        user_id = user.getUser_id();
        userRepository.save(user);

        Product product = new Product();
        product.setTitle("Laptop Gaming");
        product.setColor("Black");
        product.setDescription("Laptop gamming MSI card RTX 4090, core i12");
        productRepository.save(product);
        productId = product.getId();

        Rating rating = new Rating();
        rating.setRating(4);
        rating.setProduct(product);
        rating.setUser(user);
        ratingRepository.save(rating);
    }

    @AfterEach
    void tearDown() {
        List<Rating> ratings = ratingRepository.getAllProductRating(productId);
        ratingRepository.deleteAll(ratings);
        productRepository.deleteById(productId);
        userRepository.deleteById(user_id);
    }

    @Test
    void getAllProductRating() {
        List<Rating> ratings = ratingRepository.getAllProductRating(productId);
        assertNotNull(ratings);
        assertEquals(1, ratings.size());
    }
}