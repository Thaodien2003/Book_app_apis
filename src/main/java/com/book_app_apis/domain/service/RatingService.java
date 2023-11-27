package com.book_app_apis.domain.service;

import com.book_app_apis.application.payloads.request.RatingRequest;
import com.book_app_apis.domain.entities.Rating;
import com.book_app_apis.domain.entities.User;

import java.util.List;

public interface RatingService {

    Rating createdRating(RatingRequest request, User user);

    List<Rating> getProductRating(Long productId);

}
