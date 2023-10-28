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
import org.springframework.stereotype.Service;

@Service
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;

    protected final UserService userService;

    public CartItemServiceImpl(CartItemRepository cartItemRepository, UserService userService) {
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
    }

    @Override
    public CartItem createCartItem(CartItem cartItem) {
        cartItem.setQuantity(1);
        cartItem.setPrice(cartItem.getProduct().getPrice()* cartItem.getQuantity());
        cartItem.setDiscountedPrice(cartItem.getProduct().getDiscountedPrice());

        return this.cartItemRepository.save(cartItem);
    }

    @Override
    public CartItem updateCartItem(String userId, Long id, CartItem cartItem) throws UserException {
        CartItem item = findCartItemById(id);
        User user = this.userService.findUserById(item.getUserId());

        if(user.getUser_id().equals(userId)) {
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(item.getQuantity()*item.getProduct().getPrice());
            item.setDiscountedPrice(item.getProduct().getDiscountedPrice());
        }

        return this.cartItemRepository.save(item);
    }

    @Override
    public CartItem isCartItemExist(Cart cart, Product product, String size, String userId) {
        return this.cartItemRepository.isCartItemExist(cart, product, size, userId);
    }

    @Override
    public void removedCartItem(String userId, Long cartItemId) throws UserException {
        CartItem cartItem = findCartItemById(cartItemId);
        User user = this.userService.findUserById(cartItem.getUserId());

        User requestUser = this.userService.findUserById(userId);

        if(user.getUser_id().equals(requestUser.getUser_id())) {
            this.cartItemRepository.deleteById(cartItemId);
        } else {
            throw new UserException("You can not removed another users item");
        }
    }

    @Override
    public CartItem findCartItemById(Long cartIteId) {

        return this.cartItemRepository.findById(cartIteId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "CartItem id",cartIteId));
    }
}
