package com.book_app_apis.infrastructure.repositories;

import com.book_app_apis.domain.entities.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    @Query("SELECT r FROM Rating r WHERE r.product.id=:productId")
    List<Rating> getAllProductRating(@Param("productId") Long productId);
}
