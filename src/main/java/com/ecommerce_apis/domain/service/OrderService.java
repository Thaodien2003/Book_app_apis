package com.ecommerce_apis.domain.service;

import com.ecommerce_apis.domain.entities.Address;
import com.ecommerce_apis.domain.entities.Order;
import com.ecommerce_apis.domain.entities.User;
import com.ecommerce_apis.domain.exceptions.UserException;
import com.ecommerce_apis.presentation.dtos.OrderPlaceDTO;

import java.util.List;

public interface OrderService {
    Order createOrder(User user, Address shippingAddress);

    Order findOrderById(Long orderId);

    List<Order> userOrderHistory(String userId);

    Order placeOrder(Long orderId, OrderPlaceDTO orderPlaceDTO);

    Order confirmedOrder(Long orderId);

    Order shippedOrder(Long orderId, String shipperId) throws UserException;

    Order deliveredOrder(Long orderId, String shipperId) throws UserException;

    Order canceledOrder(Long orderId);

    Order successDelivery(Long orderId, String shipperId) throws UserException;

    List<Order> getAllOrders();

    void deletedOrder(Long orderId);
}
