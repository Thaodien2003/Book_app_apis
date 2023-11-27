package com.book_app_apis.application.serviceimpl;

import com.book_app_apis.domain.entities.*;
import com.book_app_apis.domain.exceptions.ResourceNotFoundException;
import com.book_app_apis.domain.exceptions.UserException;
import com.book_app_apis.infrastructure.repositories.AddressRepository;
import com.book_app_apis.infrastructure.repositories.OrderItemRepository;
import com.book_app_apis.infrastructure.repositories.OrderRepository;
import com.book_app_apis.infrastructure.repositories.UserRepository;
import com.book_app_apis.domain.service.CartService;
import com.book_app_apis.domain.service.OrderService;
import com.book_app_apis.presentation.dtos.OrderPlaceDTO;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
    private final MessageSource messageSource;
    private final String orderMess;
    private final String orderMessId;

    public OrderServiceImpl(OrderRepository orderRepository,
                            CartService cartService,
                            AddressRepository addressRepository,
                            UserRepository userRepository,
                            OrderItemRepository orderItemRepository,
                            MessageSource messageSource) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
        this.messageSource = messageSource;
        this.orderMess = messageSource.getMessage("order.message", null, LocaleContextHolder.getLocale());
        this.orderMessId = messageSource.getMessage("order.message.id", null, LocaleContextHolder.getLocale());
    }

    @Override
    public Order createOrder(User user, Address shippingAddress) {
        String orderCreate = messageSource.getMessage("order.create.message", null,
                LocaleContextHolder.getLocale());
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
        createOrder.setOrderStatus(orderCreate);
        createOrder.getPaymentDetails().setStatus(orderCreate);
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
                .orElseThrow(() -> new ResourceNotFoundException(orderMess, orderMessId, orderId));
    }

    @Override
    public List<Order> userOrderHistory(String userId) {
        return this.orderRepository.getUsersOrder(userId);
    }

    @Override
    public Order placeOrder(Long orderId, OrderPlaceDTO orderPlaceDTO) {
        String orderStatusPlaced = messageSource.getMessage("order.status.placed", null,
                LocaleContextHolder.getLocale());
        String orderPaymentCod = messageSource.getMessage("order.payment.cod", null, LocaleContextHolder.getLocale());
        String orderPaymentVnPay = messageSource.getMessage("order.payment.vnpay", null,
                LocaleContextHolder.getLocale());
        String paymentUnpaid = messageSource.getMessage("order.payment.status.unpaid", null,
                LocaleContextHolder.getLocale());
        String paymentComplete = messageSource.getMessage("order.payment.status.complete", null,
                LocaleContextHolder.getLocale());
        Order order = findOrderById(orderId);
        order.setOrderStatus(orderStatusPlaced);
        order.getPaymentDetails().setPaymentMethod(orderPlaceDTO.getOrderPlaceMethod());
        if (orderPaymentCod.equalsIgnoreCase(orderPlaceDTO.getOrderPlaceMethod())) {
            order.getPaymentDetails().setStatus(paymentUnpaid);
        } else if(orderPaymentVnPay.equalsIgnoreCase(orderPlaceDTO.getOrderPlaceMethod())){
            order.getPaymentDetails().setStatus(paymentComplete);
        }
        order.setPlacedOrderDate(LocalDateTime.now());
        return order;
    }

    @Override
    public Order confirmedOrder(Long orderId) {
        String orderConfirmed = messageSource.getMessage("order.status.confirmed",null,
                LocaleContextHolder.getLocale());
        Order order = findOrderById(orderId);
        order.setOrderStatus(orderConfirmed);
        return this.orderRepository.save(order);
    }

    @Override
    public Order shippedOrder(Long orderId, String shipperId) throws UserException {
        User shipper = userRepository.findById(shipperId).orElse(null);
        String shipperNotFound = messageSource.getMessage("order.error.runtime.shipper", null,
                LocaleContextHolder.getLocale());
        String orderShipped = messageSource.getMessage("order.status.shipped", null,
                LocaleContextHolder.getLocale());
        if (shipper == null) {
            throw new UserException(shipperNotFound);
        }
        Order order = findOrderById(orderId);
        order.setOrderStatus(orderShipped);
        order.setShipper(shipper);
        order.setShippedOrderDate(LocalDateTime.now());
        return this.orderRepository.save(order);
    }

    @Override
    public Order deliveredOrder(Long orderId, String shipperId) throws UserException {
        User shipper = userRepository.findById(shipperId).orElse(null);
        String shipperNotFound = messageSource.getMessage("order.error.runtime.shipper", null,
                LocaleContextHolder.getLocale());
        String orderDelivered = messageSource.getMessage("order.status.delivered", null,
                LocaleContextHolder.getLocale());
        if (shipper == null) {
            throw new UserException(shipperNotFound);
        }
        Order order = findOrderById(orderId);
        order.setOrderStatus(orderDelivered);
        order.setShipper(shipper);
        return this.orderRepository.save(order);
    }

    @Override
    public Order successDelivery(Long orderId, String shipperId) throws UserException {
        User shipper = userRepository.findById(shipperId).orElse(null);
        String shipperNotFound = messageSource.getMessage("order.error.runtime.shipper", null,
                LocaleContextHolder.getLocale());
        String successOrder = messageSource.getMessage("order.status.deliveredsuccess", null,
                LocaleContextHolder.getLocale());
        if (shipper == null) {
            throw new UserException(shipperNotFound);
        }
        Order order = findOrderById(orderId);
        order.setOrderStatus(successOrder);
        order.setSuccessDeliveryDate(LocalDateTime.now());
        return  this.orderRepository.save(order);
    }

    @Override
    public Order canceledOrder(Long orderId) {
        String canceledOrder = messageSource.getMessage("order.status.canceled", null,
                LocaleContextHolder.getLocale());
        Order order = findOrderById(orderId);
        order.setOrderStatus(canceledOrder);

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
