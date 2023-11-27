package com.ecommerce_apis.application.serviceimpl;

import com.ecommerce_apis.application.payloads.request.ReviewRequest;
import com.ecommerce_apis.domain.entities.Product;
import com.ecommerce_apis.domain.entities.Review;
import com.ecommerce_apis.domain.entities.User;
import com.ecommerce_apis.domain.exceptions.ResourceNotFoundException;
import com.ecommerce_apis.infrastructure.repositories.ProductRepository;
import com.ecommerce_apis.infrastructure.repositories.ReviewRepository;
import com.ecommerce_apis.domain.service.ReviewService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private static final Logger logger = Logger.getLogger(ReviewServiceImpl.class);

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Review createdReview(ReviewRequest request, User user) {
        Product product = this.productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", request.getProductId()));

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setReview(request.getReview());
        review.setCreatedAt(LocalDateTime.now());
        logger.info("Create review by user - " + user.getUser_id());
        return this.reviewRepository.save(review);
    }

    @Override
    public List<Review> getAllReview(Long prodctId) {
        logger.info("Get product review");
        return this.reviewRepository.getAllProductReview(prodctId);
    }
}
