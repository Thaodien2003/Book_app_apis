package com.ecommerce_apis.infrastructure.repositories;

import com.ecommerce_apis.domain.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT c FROM Cart c WHERE c.user.user_id=:userId")
    Cart findByUserId(@Param("userId") String userId);
}
