package com.book_app_apis.infrastructure.repositories;

import com.book_app_apis.domain.entities.Cart;
import com.book_app_apis.domain.entities.CartItem;
import com.book_app_apis.domain.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart=:cart "
            + "AND ci.product=:product "
            + "AND ci.size=:size "
            + "AND ci.userId=:userId")
    CartItem isCartItemExist(@Param("cart") Cart cart,
                             @Param("product") Product product,
                             @Param("size") String size,
                             @Param("userId") String userId);

    Set<CartItem> findByCart(Cart cart);
}
