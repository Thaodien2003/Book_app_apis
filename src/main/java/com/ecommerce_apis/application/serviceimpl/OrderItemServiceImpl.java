package com.ecommerce_apis.application.serviceimpl;

import com.ecommerce_apis.domain.entities.OrderItem;
import com.ecommerce_apis.infrastructure.repositories.OrderItemRepository;
import com.ecommerce_apis.domain.service.OrderItemService;
import org.springframework.stereotype.Service;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public OrderItem createOrderItem(OrderItem orderItem) {
        return this.orderItemRepository.save(orderItem);
    }
}
