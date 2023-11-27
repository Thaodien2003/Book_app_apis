package com.book_app_apis.application.serviceimpl;

import com.book_app_apis.application.payloads.request.CartItemRequest;
import com.book_app_apis.domain.entities.Cart;
import com.book_app_apis.domain.entities.CartItem;
import com.book_app_apis.domain.entities.Product;
import com.book_app_apis.domain.entities.User;
import com.book_app_apis.domain.exceptions.ResourceNotFoundException;
import com.book_app_apis.infrastructure.repositories.CartItemRepository;
import com.book_app_apis.infrastructure.repositories.CartRepository;
import com.book_app_apis.infrastructure.repositories.ProductRepository;
import com.book_app_apis.infrastructure.repositories.UserRepository;
import com.book_app_apis.domain.service.CartItemService;
import com.book_app_apis.domain.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartItemService cartItemService;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);
    private final MessageSource messageSource;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemService cartItemService,
                           CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository,
                           MessageSource messageSource) {
        this.cartRepository = cartRepository;
        this.cartItemService = cartItemService;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    @Override
    public Cart createCart(User user) {
        try {
            Cart cart = new Cart();
            cart.setUser(user);
            return this.cartRepository.save(cart);
        } catch (Exception e) {
            String createLogError = messageSource.getMessage("cart.create.log.error", null,
                    LocaleContextHolder.getLocale());
            logger.error(createLogError + "-" + user.getUser_id());
            throw e;
        }
    }

    @Override
    public void addCartItem(String userId, CartItemRequest request) {
        try {
            // Tìm User theo userId
            String userNotFound = messageSource.getMessage("cart.add.notfound", null,
                    LocaleContextHolder.getLocale());
            User user = this.userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException(userNotFound + "-" + userId));

            Cart cart = this.cartRepository.findByUserId(userId);

            if (cart == null) {
                // Tạo Cart và liên kết với User đã tồn tại
                cart = new Cart();
                cart.setUser(user);
                cart = this.cartRepository.save(cart);
            }

            Product product = this.productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "Product id", request.getProductId()));

            CartItem isExist = this.cartItemService.isCartItemExist(cart, product, request.getSize(), userId);

            if (isExist == null) {
                CartItem cartItem = new CartItem();
                cartItem.setProduct(product);
                cartItem.setCart(cart);
                cartItem.setQuantity(request.getQuantity());
                cartItem.setUserId(userId);

                int price = request.getQuantity() * product.getDiscountedPrice();
                cartItem.setPrice(price);
                cartItem.setSize(request.getSize());

                // Lưu CartItem vào cơ sở dữ liệu và nhận CartItem đã được lưu
                CartItem createItem = this.cartItemService.createCartItem(cartItem);

                // Thêm CartItem vào Cart
                cart.getCartItems().add(createItem);

                // Lưu Cart lại vào cơ sở dữ liệu
                this.cartRepository.save(cart);

                String addLogInfo = messageSource.getMessage("cart.add.log.info", null,
                        LocaleContextHolder.getLocale());
                logger.info(addLogInfo + "-" + createItem.getId());
            }
        } catch (Exception e) {
            String addLogError = messageSource.getMessage("cart.add.log.error", null,
                    LocaleContextHolder.getLocale());
            logger.error(addLogError + "-" + e.getMessage());
            throw e;
        }
    }


    @Override
    public Cart findUserCart(String userId) {
        try {
            Cart cart = this.cartRepository.findByUserId(userId);

            if (cart != null) {
                Set<CartItem> cartItems = this.cartItemRepository.findByCart(cart);
                cart.setCartItems(cartItems);

                int totalPrice = 0;
                int totalDiscountedPrice = 0;
                int totalItem = 0;

                for (CartItem cartItem : cartItems) {
                    if (cartItem.getDiscountedPrice() != null) {
                        totalPrice += cartItem.getPrice();
                        totalDiscountedPrice += cartItem.getDiscountedPrice();
                        totalItem += cartItem.getQuantity();
                    }
                }
                cart.setTotalDiscountedPrice(totalDiscountedPrice);
                cart.setTotalItem(totalItem);
                cart.setTotalPrice(totalPrice);
                cart.setDiscounte(totalPrice - totalDiscountedPrice);

            }
            String findLogInfo = messageSource.getMessage("cart.find.log.info", null,
                    LocaleContextHolder.getLocale());
            logger.info(findLogInfo + "-" + userId);
            assert cart != null;
            return this.cartRepository.save(cart);
        } catch (Exception e) {
            String findLogError = messageSource.getMessage("cart.find.log.error", null,
                    LocaleContextHolder.getLocale());
            logger.error(findLogError + "-" + e.getMessage());
            throw e;
        }
    }


}
