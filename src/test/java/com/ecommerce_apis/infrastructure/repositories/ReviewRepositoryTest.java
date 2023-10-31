package com.ecommerce_apis.infrastructure.repositories;

import com.ecommerce_apis.domain.entities.Product;
import com.ecommerce_apis.domain.entities.Review;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    private Long productId;

    @BeforeEach
    void setUp() {
        Product product = new Product();
        product.setTitle("Laptop Gaming");
        product.setColor("Black");
        product.setDescription("Laptop gamming MSI card RTX 4090, core i12");
        productRepository.save(product);
        productId = product.getId();

        Review review = new Review();
        review.setProduct(product);
        review.setReview("Laptop gmaing very good");
        reviewRepository.save(review);
    }

    @AfterEach
    void tearDown() {
        List<Review> reviews = reviewRepository.getAllProductReview(productId);
        reviewRepository.deleteAll(reviews);
        productRepository.deleteById(productId);
    }

    @Test
    void testGetAllProductReview() {
        List<Review> reviews = reviewRepository.getAllProductReview(productId);
        assertNotNull(reviews);
        assertEquals(1, reviews.size());
    }
}