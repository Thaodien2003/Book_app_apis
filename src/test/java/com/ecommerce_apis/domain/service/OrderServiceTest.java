package com.ecommerce_apis.domain.service;

import com.ecommerce_apis.domain.entities.*;
import com.ecommerce_apis.domain.exceptions.ResourceNotFoundException;
import com.ecommerce_apis.infrastructure.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    String user_id;
    Long order_id;

    @Test
    void findOrderById() {
        assertDoesNotThrow(() -> orderService.findOrderById(2L));
    }

    @Test
    void findOrderByIdWithInvalidOrderId() {
        Long nonExistOrder_id = 99L;
        assertThrows(ResourceNotFoundException.class ,() -> orderService.findOrderById(nonExistOrder_id));
    }

    @Test
    void userOrderHistory() {
        List<Order> orders = orderService.userOrderHistory(user_id);
        assertNotNull(orders);
    }

    @Test
    void placeOrder() {
        Order order = new Order();
        order.setOrderStatus("PLACED");
        order.getPaymentDetails().setStatus("COMPLETED");
        orderRepository.save(order);
        order_id = order.getId();

        Order placedOrder = orderService.placeOrder(order_id);

        assertEquals("PLACED", placedOrder.getOrderStatus());
        assertEquals("COMPLETED", placedOrder.getPaymentDetails().getStatus());
    }

    @Test
    void placedOrderWithInvalidOrderId() {
        Long nonExistOrder_id = 99L;
        assertThrows(ResourceNotFoundException.class ,() -> orderService.placeOrder(nonExistOrder_id));
    }

    @Test
    void confirmedOrder() {
        Order order = new Order();
        order.setOrderStatus("CONFIRMED");
        orderRepository.save(order);
        order_id = order.getId();

        Order placedOrder = orderService.confirmedOrder(order_id);

        assertEquals("CONFIRMED", placedOrder.getOrderStatus());
    }

    @Test
    void confirmedOrderWithInvalidOrderId() {
        Long nonExistOrder_id = 99L;
        assertThrows(ResourceNotFoundException.class ,() -> orderService.confirmedOrder(nonExistOrder_id));
    }

    @Test
    void shippedOrder() {
        Order order = new Order();
        order.setOrderStatus("SHIPPED");
        orderRepository.save(order);
        order_id = order.getId();

        Order placedOrder = orderService.shippedOrder(order_id);

        assertEquals("SHIPPED", placedOrder.getOrderStatus());
    }

    @Test
    void shippedOrderWithInvalidOrderId() {
        Long nonExistOrder_id = 99L;
        assertThrows(ResourceNotFoundException.class ,() -> orderService.shippedOrder(nonExistOrder_id));
    }

    @Test
    void deliveredOrder() {
        Order order = new Order();
        order.setOrderStatus("DELIVERED");
        orderRepository.save(order);
        order_id = order.getId();

        Order placedOrder = orderService.deliveredOrder(order_id);

        assertEquals("DELIVERED", placedOrder.getOrderStatus());
    }

    @Test
    void deliveredOrderWithInvalidOrderId() {
        Long nonExistOrder_id = 99L;
        assertThrows(ResourceNotFoundException.class ,() -> orderService.deliveredOrder(nonExistOrder_id));
    }

    @Test
    void canceledOrder() {
        Order order = new Order();
        order.setOrderStatus("CACELED");
        orderRepository.save(order);
        order_id = order.getId();

        Order placedOrder = orderService.canceledOrder(order_id);

        assertEquals("CACELED", placedOrder.getOrderStatus());
    }

    @Test
    void cancelOrderWithInvalidOrderId() {
        Long nonExistOrder_id = 99L;
        assertThrows(ResourceNotFoundException.class ,() -> orderService.canceledOrder(nonExistOrder_id));
    }

    @Test
    void getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        assertNotNull(orders);
    }

    @Test
    void deletedOrder() {
        assertDoesNotThrow(() -> orderService.deletedOrder(2L));
    }

    @Test
    void deleteOrderFailed() {
        Long nonExistOrder_id = 99L;
        assertThrows(ResourceNotFoundException.class ,() -> orderService.deletedOrder(nonExistOrder_id));
    }
}