package com.ecommerce_apis.application.serviceimpl;

import com.ecommerce_apis.domain.entities.*;
import com.ecommerce_apis.domain.exceptions.ResourceNotFoundException;
import com.ecommerce_apis.infrastructure.repositories.AddressRepository;
import com.ecommerce_apis.infrastructure.repositories.OrderItemRepository;
import com.ecommerce_apis.infrastructure.repositories.OrderRepository;
import com.ecommerce_apis.infrastructure.repositories.UserRepository;
import com.ecommerce_apis.domain.service.CartService;
import com.ecommerce_apis.domain.service.OrderService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final CartService cartService;

    private final AddressRepository addressRepository;

    private final UserRepository userRepository;

    private final OrderItemRepository orderItemRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            CartService cartService,
                            AddressRepository addressRepository,
                            UserRepository userRepository,
                            OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public Order createOrder(User user, Address shippingAddress) {

        shippingAddress.setUser(user);
        Address address = this.addressRepository.save(shippingAddress);
        user.getAddress().add(address);
        this.userRepository.save(user);

        Cart cart = this.cartService.findUserCart(user.getUser_id());
        List<OrderItem> orderItems = new ArrayList<>();

        for(CartItem item : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setPrice(item.getPrice());
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setSize(item.getSize());
            orderItem.setUserId(item.getUserId());
            orderItem.setDiscountedPrice(item.getDiscountedPrice());

            OrderItem createOrderItem = this.orderItemRepository.save(orderItem);
            orderItems.add(createOrderItem);
        }

        Order createOrder = new Order();
        createOrder.setUser(user);
        createOrder.setOrderItems(orderItems);
        createOrder.setTotalPrice(cart.getTotalPrice());
        createOrder.setTotalDiscountedPrice(cart.getTotalDiscountedPrice());
        createOrder.setDiscounte(cart.getDiscounte());
        createOrder.setTotalItem(cart.getTotalItem());

        createOrder.setShippingAddress(address);
        createOrder.setOrderDate(LocalDateTime.now());
        createOrder.setOrderStatus("PENDING");
        createOrder.getPaymentDetails().setStatus("PENDING");
        createOrder.setCeatedAt(LocalDateTime.now());

        Order savedOrder = this.orderRepository.save(createOrder);

        for(OrderItem item : orderItems) {
            item.setOrder(savedOrder);
            this.orderItemRepository.save(item);
        }

        return savedOrder;
    }

    @Override
    public Order findOrderById(Long orderId) {

        return this.orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "Order id", orderId));
    }

    @Override
    public List<Order> userOrderHistory(String userId) {
        return this.orderRepository.getUsersOrder(userId);
    }

    @Override
    public Order placeOrder(Long orderId) {
        Order order = findOrderById(orderId);
        order.setOrderStatus("PLACED");
        order.getPaymentDetails().setStatus("COMPLETED");
        return order;
    }

    @Override
    public Order confirmedOrder(Long orderId) {
        Order order = findOrderById(orderId);
        order.setOrderStatus("CONFIRMED");

        return this.orderRepository.save(order);
    }

    @Override
    public Order shippedOrder(Long orderId) {
        Order order = findOrderById(orderId);
        order.setOrderStatus("SHIPPED");
        return this.orderRepository.save(order);
    }

    @Override
    public Order deliveredOrder(Long orderId) {
        Order order = findOrderById(orderId);
        order.setOrderStatus("DELIVERED");
        order.setDeliveryDate(LocalDateTime.now());

        return this.orderRepository.save(order);
    }

    @Override
    public Order canceledOrder(Long orderId) {
        Order order = findOrderById(orderId);
        order.setOrderStatus("CACELED");

        return this.orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return this.orderRepository.findAll();
    }

    @Override
    public void deletedOrder(Long orderId) {
        Order order = findOrderById(orderId);
        this.orderRepository.delete(order);
    }
}
