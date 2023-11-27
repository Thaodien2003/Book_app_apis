package com.book_app_apis.domain.service;

import com.book_app_apis.application.payloads.request.ReviewRequest;
import com.book_app_apis.domain.entities.Review;
import com.book_app_apis.domain.entities.User;

import java.util.List;

public interface ReviewService {

    Review createdReview(ReviewRequest request, User user);

    List<Review> getAllReview(Long prodctId);

}
