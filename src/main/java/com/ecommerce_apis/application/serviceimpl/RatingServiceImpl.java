package com.ecommerce_apis.application.serviceimpl;

import com.ecommerce_apis.application.payloads.request.RatingRequest;
import com.ecommerce_apis.domain.entities.Product;
import com.ecommerce_apis.domain.entities.Rating;
import com.ecommerce_apis.domain.entities.User;
import com.ecommerce_apis.domain.exceptions.ResourceNotFoundException;
import com.ecommerce_apis.infrastructure.repositories.ProductRepository;
import com.ecommerce_apis.infrastructure.repositories.RatingRepository;
import com.ecommerce_apis.domain.service.RatingService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final ProductRepository productRepository;
    private static final Logger logger = Logger.getLogger(RatingServiceImpl.class);

    public RatingServiceImpl(RatingRepository ratingRepository, ProductRepository productRepository) {
        this.ratingRepository = ratingRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Rating createdRating(RatingRequest request, User user) {
        Product product = this.productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", request.getProductId()));

        Rating rating = new Rating();

        rating.setProduct(product);
        rating.setUser(user);
        rating.setRating(request.getRating());
        rating.setCreatedAt(LocalDateTime.now());
        logger.info("Create rating by user - "+user.getUser_id());
        return this.ratingRepository.save(rating);
    }

    @Override
    public List<Rating> getProductRating(Long productId) {
        logger.info("Get product rating");
        return this.ratingRepository.getAllProductRating(productId);
    }
}
