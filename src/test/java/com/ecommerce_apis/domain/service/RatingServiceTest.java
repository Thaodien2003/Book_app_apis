package com.ecommerce_apis.domain.service;

import com.ecommerce_apis.application.payloads.request.RatingRequest;
import com.ecommerce_apis.domain.entities.Product;
import com.ecommerce_apis.domain.entities.Rating;
import com.ecommerce_apis.domain.entities.User;
import com.ecommerce_apis.domain.exceptions.ResourceNotFoundException;
import com.ecommerce_apis.infrastructure.repositories.ProductRepository;
import com.ecommerce_apis.infrastructure.repositories.RatingRepository;
import com.ecommerce_apis.infrastructure.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RatingServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RatingService ratingService;

    private User user;
    private Product product;
    private RatingRequest request;

    String user_id;
    Long product_id;
    Long rating_id;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("Test");
        user.setEmail("test@gmail.com");
        userRepository.save(user);
        user_id = user.getUser_id();

        product = new Product();
        product.setTitle("Laptop gaming MSI");
        productRepository.save(product);
        product_id = product.getId();

        request = new RatingRequest();
        request.setProductId(product_id);
        request.setRating(5);
    }

    @AfterEach
    void tearDown() {
        if (user != null && user.getUser_id() != null) {
            userRepository.deleteById(user.getUser_id());
        }

        if(product != null && product.getId() != null) {
            productRepository.deleteById(product_id);
        }

        if(rating_id != null) {
            ratingRepository.deleteById(rating_id);
        }
    }

    @Test
    void createdRating() {
        Rating rating = ratingService.createdRating(request, user);
        rating_id = rating.getId();
        assertEquals(user, rating.getUser());
        assertEquals(product.getId(), rating.getProduct().getId());
        assertEquals(request.getRating(), rating.getRating());
    }

    @Test
    public void testCreatedRatingWithInvalidProduct() {
        User user = new User();
        RatingRequest ratingRequest = new RatingRequest();
        ratingRequest.setProductId(1L);
        ratingRequest.setRating(4);
        assertThrows(ResourceNotFoundException.class, () -> ratingService.createdRating(ratingRequest, user));
    }

    @Test
    void getProductRating() {
        List<Rating> ratingList = new ArrayList<>();
        List<Rating> ratings = ratingRepository.getAllProductRating(product_id);
        assertEquals(ratingList, ratings);
    }
}