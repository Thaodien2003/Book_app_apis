package com.ecommerce_apis.domain.repositories;

import com.ecommerce_apis.domain.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.product.id=:productId")
    List<Review> getAllProductReview(@Param("productId") Long productId);

}
