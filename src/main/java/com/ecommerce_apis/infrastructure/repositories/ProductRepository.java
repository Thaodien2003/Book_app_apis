package com.ecommerce_apis.infrastructure.repositories;

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


}
