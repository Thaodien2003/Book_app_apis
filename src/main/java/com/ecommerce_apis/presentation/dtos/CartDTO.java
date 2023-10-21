package com.ecommerce_apis.presentation.dtos;

import com.ecommerce_apis.domain.entities.CartItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Long cartId;
    private UserDTO user;
    private Set<CartItem> cartItems = new HashSet<>();
    private int totalPrice;
    private int totalItem;
    private int totalDiscountedPrice;
    private int discounte;
}
