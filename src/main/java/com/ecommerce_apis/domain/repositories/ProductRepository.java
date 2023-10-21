package com.ecommerce_apis.domain.repositories;

import com.ecommerce_apis.domain.entities.Category;
import com.ecommerce_apis.domain.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findProductByCategory(Category category);

    @Query("SELECT p FROM Product p WHERE p.title like :key")
    List<Product> findByTitle(@Param("key") String title);

//    @Query("SELECT p FROM Product p " +
//            "WHERE (:categoryId IS NULL OR p.category.id = :categoryId) " +
//            "AND ((:minPrice IS NULL AND :maxPrice IS NULL) OR (p.discountedPrice BETWEEN :minPrice AND :maxPrice)) " +
//            "AND (:minDiscount IS NULL OR p.discountPersent >= :minDiscount) " +
//            "ORDER BY " +
//            "CASE WHEN :sort = 'price_low' THEN p.discountedPrice END ASC, " +
//            "CASE WHEN :sort = 'price_high' THEN p.discountedPrice END DESC")
//    List<Product> filterProducts(@Param("category") Long categoryId,
//                                           @Param("minPrice") Integer minPrice,
//                                           @Param("maxPrice") Integer maxPrice,
//                                           @Param("minDiscount") Integer minDiscount,
//                                           @Param("sort") String sort);
}