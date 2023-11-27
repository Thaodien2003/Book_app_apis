package com.book_app_apis.application.serviceimpl;

import com.book_app_apis.domain.entities.OrderItem;
import com.book_app_apis.infrastructure.repositories.OrderItemRepository;
import com.book_app_apis.domain.service.OrderItemService;
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
