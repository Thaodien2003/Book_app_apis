package com.book_app_apis.application.serviceimpl;

import com.book_app_apis.application.payloads.request.RatingRequest;
import com.book_app_apis.domain.entities.Product;
import com.book_app_apis.domain.entities.Rating;
import com.book_app_apis.domain.entities.User;
import com.book_app_apis.domain.exceptions.ResourceNotFoundException;
import com.book_app_apis.domain.service.RatingService;
import com.book_app_apis.infrastructure.repositories.ProductRepository;
import com.book_app_apis.infrastructure.repositories.RatingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final ProductRepository productRepository;
    private final MessageSource messageSource;
    private final String productMess;
    private final String productMessId;
    private static final Logger logger = LoggerFactory.getLogger(RatingServiceImpl.class);

    public RatingServiceImpl(RatingRepository ratingRepository, ProductRepository productRepository, MessageSource messageSource) {
        this.ratingRepository = ratingRepository;
        this.productRepository = productRepository;
        this.messageSource = messageSource;
        this.productMess = messageSource.getMessage("product.message", null,
                LocaleContextHolder.getLocale());
        this.productMessId = messageSource.getMessage("product.message.id", null,
                LocaleContextHolder.getLocale());
    }

    @Override
    public Rating createdRating(RatingRequest request, User user) {
        Product product = this.productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(productMess, productMessId, request.getProductId()));
        String logInfo = messageSource.getMessage("rating.create.log.info", null,
                LocaleContextHolder.getLocale());
        double ratingValue = request.getRating();
        if (ratingValue < 1 || ratingValue > 5) {
            String logErrorRating = messageSource.getMessage("rating.log.error", null, LocaleContextHolder.getLocale());
            throw new IllegalArgumentException(logErrorRating);
        }

        Rating rating = new Rating();

        rating.setProduct(product);
        rating.setUser(user);
        rating.setRating(request.getRating());
        rating.setCreatedAt(LocalDateTime.now());
        logger.info(logInfo + "-" + user.getUser_id());

        return this.ratingRepository.save(rating);
    }


    @Override
    public List<Rating> getProductRating(Long productId) {
        String getProductRating = messageSource.getMessage("rating.getall.log.info", null,
                LocaleContextHolder.getLocale());
        logger.info(getProductRating);
        return this.ratingRepository.getAllProductRating(productId);
    }
}
