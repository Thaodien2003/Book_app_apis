package com.ecommerce_apis.infrastructure.repositories;

import com.ecommerce_apis.domain.entities.Cart;
import com.ecommerce_apis.domain.entities.CartItem;
import com.ecommerce_apis.domain.entities.Product;
import com.ecommerce_apis.domain.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CartItemRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    String user_id;
    Long cart_id;
    Long product_id;
    Long cartItem_id;
    String size;

    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("Test");
        user.setEmail("test@gamil.com");
        userRepository.save(user);
        user_id = user.getUser_id();

        product = new Product();
        product.setTitle("Laptop dell gmaing");
        productRepository.save(product);
        product_id = product.getId();

        cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);
        cart_id = cart.getId();

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setUserId(user_id);
        cartItem.setSize("15.6 inch");
        cartItemRepository.save(cartItem);
        cartItem_id = cartItem.getId();
        size = cartItem.getSize();
    }

    @AfterEach
    void tearDown() {
        cartItemRepository.deleteById(cartItem_id);
        cartRepository.deleteById(cart_id);
        productRepository.deleteById(product_id);
        userRepository.deleteById(user_id);
    }

    @Test
    void isCartItemExist() {
        CartItem cartItem = cartItemRepository.isCartItemExist(cart, product, size, user_id);
        assertNotNull(cartItem);
    }

    @Test
    void findByCart() {
        Set<CartItem> cartItems = cartItemRepository.findByCart(cart);
        assertNotNull(cartItems);
        assertEquals(1, cartItems.size());
    }
}