package com.ecommerce_apis.domain.service.impl;

import com.ecommerce_apis.application.payloads.request.RatingRequest;
import com.ecommerce_apis.domain.entities.Product;
import com.ecommerce_apis.domain.entities.Rating;
import com.ecommerce_apis.domain.entities.User;
import com.ecommerce_apis.domain.exceptions.ResourceNotFoundException;
import com.ecommerce_apis.domain.repositories.ProductRepository;
import com.ecommerce_apis.domain.repositories.RatingRepository;
import com.ecommerce_apis.domain.service.RatingService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;

    private final ProductRepository productRepository;

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

        return this.ratingRepository.save(rating);
    }

    @Override
    public List<Rating> getProductRating(Long productId) {
        return this.ratingRepository.getAllProductRating(productId);
    }
}
