package com.ecommerce_apis.infrastructure.repositories;

import com.ecommerce_apis.domain.entities.Order;
import com.ecommerce_apis.domain.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    String user_id;
    Long order_id;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("Test");
        user.setEmail("test@gmail.com");
        userRepository.save(user);
        user_id = user.getUser_id();

        Order order = new Order();
        order.setOrderStatus("PLACED");
        order.setUser(user);
        order.setTotalPrice(10000000);
        orderRepository.save(order);
        order_id = order.getId();
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteById(order_id);
        userRepository.deleteById(user_id);
    }

    @Test
    void getUsersOrder() {
        List<Order> orders = orderRepository.getUsersOrder(user_id);
        assertNotNull(orders);
        assertEquals(1, orders.size());
    }
}