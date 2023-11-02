package com.ecommerce_apis.domain.service;

import com.ecommerce_apis.application.payloads.request.CartItemRequest;
import com.ecommerce_apis.domain.entities.Cart;
import com.ecommerce_apis.domain.entities.User;

public interface CartService {

    //create cart
    Cart createCart(User user);

    //add cartitem
    void addCartItem(String userId, CartItemRequest request);

    //get cart by user
    Cart findUserCart(String userId);
}
