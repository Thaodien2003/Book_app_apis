package com.ecommerce_apis.domain.repositories;

import com.ecommerce_apis.domain.entities.Rating;
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
