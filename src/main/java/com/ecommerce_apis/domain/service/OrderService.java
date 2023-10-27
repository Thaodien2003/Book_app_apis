package com.ecommerce_apis.domain.service;

import com.ecommerce_apis.domain.entities.Address;
import com.ecommerce_apis.domain.entities.Order;
import com.ecommerce_apis.domain.entities.User;

import java.util.List;

public interface OrderService {
    Order createOrder(User user, Address shippingAddress);

    Order findOrderById(Long orderId);

    List<Order> userOrderHistory(String userId);

    Order placeOrder(Long orderId);

    Order confirmedOrder(Long orderId);

    Order shippedOrder(Long orderId);

    Order deliveredOrder(Long orderId);

    Order canceledOrder(Long orderId);

    List<Order> getAllOrders();

    void deletedOrder(Long orderId);
}
