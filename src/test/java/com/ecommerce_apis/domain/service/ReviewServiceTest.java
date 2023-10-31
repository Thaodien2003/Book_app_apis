package com.ecommerce_apis.domain.service;

import com.ecommerce_apis.application.payloads.request.ReviewRequest;
import com.ecommerce_apis.domain.entities.Product;
import com.ecommerce_apis.domain.entities.Review;
import com.ecommerce_apis.domain.entities.User;
import com.ecommerce_apis.domain.exceptions.ResourceNotFoundException;
import com.ecommerce_apis.infrastructure.repositories.ProductRepository;
import com.ecommerce_apis.infrastructure.repositories.ReviewRepository;
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
class ReviewServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewService reviewService;

    private ReviewRequest request;

    private User user;

    private Product product;

    String user_id;
    Long product_id;
    Long review_id;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@gmail.com");
        user.setUsername("Test");
        userRepository.save(user);
        user_id = user.getUser_id();

        product = new Product();
        product.setTitle("Laptop gaming");
        productRepository.save(product);
        product_id = product.getId();

        request = new ReviewRequest();
        request.setProductId(product_id);
        request.setReview("This is a review test");
    }

    @AfterEach
    void tearDown() {
        if (user != null && user.getUser_id() != null) {
            userRepository.deleteById(user.getUser_id());
        }

        if(product != null && product.getId() != null) {
            productRepository.deleteById(product_id);
        }

        if(review_id != null) {
            reviewRepository.deleteById(review_id);
        }
    }

    @Test
    void createdReview() {
        Review review = reviewService.createdReview(request, user);
        review_id = review.getId();
        assertEquals(user, review.getUser());
        assertEquals(product.getId(), review.getProduct().getId());
        assertEquals(request.getReview(), review.getReview());
    }

    @Test
    public void testCreatedReviewWithInvalidProduct() {
        User user = new User();
        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setProductId(1L);
        reviewRequest.setReview("This is a review");
        assertThrows(ResourceNotFoundException.class, () -> reviewService.createdReview(reviewRequest, user));
    }

    @Test
    void getAllReview() {
        List<Review> reviews = new ArrayList<>();
        List<Review> result = reviewService.getAllReview(product_id);
        assertEquals(reviews, result);
    }
}