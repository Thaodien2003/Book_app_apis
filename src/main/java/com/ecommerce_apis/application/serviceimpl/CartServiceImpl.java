package com.ecommerce_apis.application.serviceimpl;

import com.ecommerce_apis.application.payloads.request.CartItemRequest;
import com.ecommerce_apis.domain.entities.Cart;
import com.ecommerce_apis.domain.entities.CartItem;
import com.ecommerce_apis.domain.entities.Product;
import com.ecommerce_apis.domain.entities.User;
import com.ecommerce_apis.domain.exceptions.ResourceNotFoundException;
import com.ecommerce_apis.infrastructure.repositories.CartItemRepository;
import com.ecommerce_apis.infrastructure.repositories.CartRepository;
import com.ecommerce_apis.infrastructure.repositories.ProductRepository;
import com.ecommerce_apis.infrastructure.repositories.UserRepository;
import com.ecommerce_apis.domain.service.CartItemService;
import com.ecommerce_apis.domain.service.CartService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final UserRepository userRepository;

    private final CartItemService cartItemService;

    private final ProductRepository productRepository;

    private final CartItemRepository cartItemRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemService cartItemService,
                           CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemService = cartItemService;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return this.cartRepository.save(cart);
    }

    @Override
    public String addCartItem(String userId, CartItemRequest request) {
        // Tìm User theo userId
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id" +userId+ "not found"));

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
        }
        return "Item added to cart";
    }


    @Override
    public Cart findUserCart(String userId) {
        Cart cart = this.cartRepository.findByUserId(userId);

        if(cart != null) {
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

        assert cart != null;
        return this.cartRepository.save(cart);
    }
}
