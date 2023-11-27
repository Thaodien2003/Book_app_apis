package com.ecommerce_apis.application.serviceimpl;

import com.ecommerce_apis.domain.entities.Cart;
import com.ecommerce_apis.domain.entities.CartItem;
import com.ecommerce_apis.domain.entities.Product;
import com.ecommerce_apis.domain.entities.User;
import com.ecommerce_apis.domain.exceptions.ResourceNotFoundException;
import com.ecommerce_apis.domain.exceptions.UserException;
import com.ecommerce_apis.infrastructure.repositories.CartItemRepository;
import com.ecommerce_apis.domain.service.CartItemService;
import com.ecommerce_apis.domain.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    protected final UserService userService;
    private static final Logger logger = Logger.getLogger(CartItemServiceImpl.class);

    public CartItemServiceImpl(CartItemRepository cartItemRepository, UserService userService) {
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
    }

    @Override
    public CartItem createCartItem(CartItem cartItem) {
        try {
            cartItem.setQuantity(1);
            cartItem.setPrice(cartItem.getProduct().getPrice() * cartItem.getQuantity());
            cartItem.setDiscountedPrice(cartItem.getProduct().getDiscountedPrice());

            return this.cartItemRepository.save(cartItem);
        } catch (Exception e) {
            logger.error("Failed to create cart item: {}" + e.getMessage());
            throw e;
        }
    }

    @Override
    public CartItem updateCartItem(String userId, Long id, CartItem cartItem) throws UserException {
        try {
            CartItem item = findCartItemById(id);
            User user = this.userService.findUserById(item.getUserId());

            if (user.getUser_id().equals(userId)) {
                item.setQuantity(cartItem.getQuantity());
                item.setPrice(item.getQuantity() * item.getProduct().getPrice());
                item.setDiscountedPrice(item.getProduct().getDiscountedPrice());
            }

            return this.cartItemRepository.save(item);
        } catch (Exception e) {
            logger.error("Failed to update cart item: {}" + e.getMessage());
            throw e;
        }
    }

    @Override
    public CartItem isCartItemExist(Cart cart, Product product, String size, String userId) {
        try {
            return this.cartItemRepository.isCartItemExist(cart, product, size, userId);
        } catch (Exception e) {
            logger.error("Failed to check if cart item exists: {}" + e.getMessage());
            throw e;
        }
    }

    @Override
    public void removedCartItem(String userId, Long cartItemId) throws UserException {
        try {
            CartItem cartItem = findCartItemById(cartItemId);
            User user = this.userService.findUserById(cartItem.getUserId());
            User requestUser = this.userService.findUserById(userId);

            if (user.getUser_id().equals(requestUser.getUser_id())) {
                this.cartItemRepository.deleteById(cartItemId);
            } else {
                throw new UserException("You cannot remove another user's item");
            }
        } catch (Exception e) {
            logger.error("Failed to remove cart item: {}" + e.getMessage());
            throw e;
        }
    }

    @Override
    public CartItem findCartItemById(Long cartIteId) {
        try {
            return this.cartItemRepository.findById(cartIteId)
                    .orElseThrow(() -> new ResourceNotFoundException("CartItem", "CartItem id", cartIteId));
        } catch (Exception e) {
            logger.error("Failed to find cart item by id: {}" + e.getMessage());
            throw e;
        }
    }
}
