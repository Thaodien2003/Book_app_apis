package com.book_app_apis.domain.service;

import com.book_app_apis.domain.entities.Address;
import com.book_app_apis.domain.entities.Order;
import com.book_app_apis.domain.entities.User;
import com.book_app_apis.domain.exceptions.UserException;
import com.book_app_apis.presentation.dtos.OrderPlaceDTO;

import java.util.List;

public interface OrderService {
    //create order
    Order createOrder(User user, Address shippingAddress);

    //find order by id
    Order findOrderById(Long orderId);

    //order history
    List<Order> userOrderHistory(String userId);

    //placed order
    Order placeOrder(Long orderId, OrderPlaceDTO orderPlaceDTO);

    //confirmed order
    Order confirmedOrder(Long orderId);

    //shipped order
    Order shippedOrder(Long orderId, String shipperId) throws UserException;

    //delivered order
    Order deliveredOrder(Long orderId, String shipperId) throws UserException;

    //cancaled order
    Order canceledOrder(Long orderId);

    //success delivery
    Order successDelivery(Long orderId, String shipperId) throws UserException;

    //get all orders
    List<Order> getAllOrders();

    //deleted orders
    void deletedOrder(Long orderId);
}
