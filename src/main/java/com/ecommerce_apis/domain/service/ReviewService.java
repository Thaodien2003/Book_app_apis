package com.ecommerce_apis.domain.service;

import com.ecommerce_apis.application.payloads.request.ReviewRequest;
import com.ecommerce_apis.domain.entities.Review;
import com.ecommerce_apis.domain.entities.User;

import java.util.List;

public interface ReviewService {

    Review createdReview(ReviewRequest request, User user);

    List<Review> getAllReview(Long prodctId);

}
