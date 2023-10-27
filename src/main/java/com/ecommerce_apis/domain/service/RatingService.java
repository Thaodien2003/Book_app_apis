package com.ecommerce_apis.domain.service;

import com.ecommerce_apis.application.payloads.request.RatingRequest;
import com.ecommerce_apis.domain.entities.Rating;
import com.ecommerce_apis.domain.entities.User;

import java.util.List;

public interface RatingService {

    Rating createdRating(RatingRequest request, User user);

    List<Rating> getProductRating(Long productId);

}
