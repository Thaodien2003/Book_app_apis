package com.book_app_apis.application.serviceimpl;

import com.book_app_apis.application.payloads.request.ReviewRequest;
import com.book_app_apis.domain.entities.Product;
import com.book_app_apis.domain.entities.Review;
import com.book_app_apis.domain.entities.User;
import com.book_app_apis.domain.exceptions.ResourceNotFoundException;
import com.book_app_apis.infrastructure.repositories.ProductRepository;
import com.book_app_apis.infrastructure.repositories.ReviewRepository;
import com.book_app_apis.domain.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final MessageSource messageSource;
    private final String productMess;
    private final String productMessId;
    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             ProductRepository productRepository,
                             MessageSource messageSource) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.messageSource = messageSource;
        this.productMess = messageSource.getMessage("product.message", null,
                LocaleContextHolder.getLocale());
        this.productMessId = messageSource.getMessage("product.message.id", null,
                LocaleContextHolder.getLocale());
    }

    @Override
    public Review createdReview(ReviewRequest request, User user) {
        Product product = this.productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(productMess, productMessId, request.getProductId()));
        String logInfo = messageSource.getMessage("review.create.log.info", null,
                LocaleContextHolder.getLocale());
        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setReview(request.getReview());
        review.setCreatedAt(LocalDateTime.now());
        logger.info(logInfo + "-" + user.getUser_id());
        return this.reviewRepository.save(review);
    }

    @Override
    public List<Review> getAllReview(Long prodctId) {
        String getProductReview = messageSource.getMessage("review.getall.log.info", null,
                LocaleContextHolder.getLocale());
        logger.info(getProductReview);
        return this.reviewRepository.getAllProductReview(prodctId);
    }
}
