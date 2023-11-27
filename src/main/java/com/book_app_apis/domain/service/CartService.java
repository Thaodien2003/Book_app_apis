package com.book_app_apis.domain.service;

import com.book_app_apis.application.payloads.request.CartItemRequest;
import com.book_app_apis.domain.entities.Cart;
import com.book_app_apis.domain.entities.User;

public interface CartService {
    //create cart
    Cart createCart(User user);

    //add cartitem
    void addCartItem(String userId, CartItemRequest request);

    //get cart by user
    Cart findUserCart(String userId);
}
