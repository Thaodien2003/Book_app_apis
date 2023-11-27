package com.book_app_apis.domain.service;

import com.book_app_apis.domain.entities.Cart;
import com.book_app_apis.domain.entities.CartItem;
import com.book_app_apis.domain.entities.Product;
import com.book_app_apis.domain.exceptions.UserException;

public interface CartItemService {
    //create cartitem
    CartItem createCartItem(CartItem cartItem);

    //update cartitem
    CartItem updateCartItem(String userId, Long id, CartItem cartItem) throws UserException;

    CartItem isCartItemExist(Cart cart, Product product, String size, String userId);

    //removed cartitem
    void removedCartItem(String userId, Long cartItemId) throws UserException;

    //get cartitem by id
    CartItem findCartItemById(Long cartIteId);
}
