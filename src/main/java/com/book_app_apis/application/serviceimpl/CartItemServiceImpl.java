package com.book_app_apis.application.serviceimpl;

import com.book_app_apis.domain.entities.Cart;
import com.book_app_apis.domain.entities.CartItem;
import com.book_app_apis.domain.entities.Product;
import com.book_app_apis.domain.entities.User;
import com.book_app_apis.domain.exceptions.ResourceNotFoundException;
import com.book_app_apis.domain.exceptions.UserException;
import com.book_app_apis.domain.service.CartItemService;
import com.book_app_apis.domain.service.UserService;
import com.book_app_apis.infrastructure.repositories.CartItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    protected final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(CartItemServiceImpl.class);
    private final MessageSource messageSource;

    public CartItemServiceImpl(CartItemRepository cartItemRepository, UserService userService, MessageSource messageSource) {
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
        this.messageSource = messageSource;
    }

    @Override
    public CartItem createCartItem(CartItem cartItem) {
        try {
            cartItem.setQuantity(1);
            cartItem.setPrice(cartItem.getProduct().getPrice() * cartItem.getQuantity());
            cartItem.setDiscountedPrice(cartItem.getProduct().getDiscountedPrice());

            return this.cartItemRepository.save(cartItem);
        } catch (Exception e) {
            String messageCreateLogError = messageSource.getMessage("cartitem.create.message.log.error", null,
                    LocaleContextHolder.getLocale());
            logger.error(messageCreateLogError + "-" + e.getMessage());
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
            String messageUpdateLogError = messageSource.getMessage("cartitem.update.message.log.error", null,
                    LocaleContextHolder.getLocale());
            logger.error(messageUpdateLogError + "-" + e.getMessage());
            throw e;
        }
    }

    @Override
    public CartItem isCartItemExist(Cart cart, Product product, String size, String userId) {
        try {
            return this.cartItemRepository.isCartItemExist(cart, product, size, userId);
        } catch (Exception e) {
            String existLogError = messageSource.getMessage("cartitem.exists.log.error", null,
                    LocaleContextHolder.getLocale());
            logger.error(existLogError + "-" + e.getMessage());
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
                String errorRuntimeRemoved = messageSource.getMessage("cartitem.removed.runtime", null,
                        LocaleContextHolder.getLocale());
                throw new UserException(errorRuntimeRemoved);
            }
        } catch (Exception e) {
            String removedLogError = messageSource.getMessage("cartitem.removed.log.error", null,
                    LocaleContextHolder.getLocale());
            logger.error(removedLogError + "-" + e.getMessage());
            throw e;
        }
    }

    @Override
    public CartItem findCartItemById(Long cartIteId) {
        try {
            return this.cartItemRepository.findById(cartIteId)
                    .orElseThrow(() -> new ResourceNotFoundException("CartItem", "CartItem id", cartIteId));
        } catch (Exception e) {
            String findLogError = messageSource.getMessage("cartitem.find.log.error", null,
                    LocaleContextHolder.getLocale());
            logger.error(findLogError + "-" + e.getMessage());
            throw e;
        }
    }
}
