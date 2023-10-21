package com.ecommerce_apis.domain.service;

import com.ecommerce_apis.domain.entities.Cart;
import com.ecommerce_apis.domain.entities.CartItem;
import com.ecommerce_apis.domain.entities.Product;
import com.ecommerce_apis.domain.exceptions.UserException;

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
