package com.book_app_apis.presentation.controllers.admin;

import com.book_app_apis.application.payloads.response.ApiResponse;
import com.book_app_apis.domain.entities.Order;
import com.book_app_apis.domain.service.OrderService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;
    private final MessageSource messageSource;

    public AdminOrderController(OrderService orderService, MessageSource messageSource) {
        this.orderService = orderService;
        this.messageSource = messageSource;
    }

    @GetMapping("/")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = this.orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PutMapping("/{orderId}/confirmed")
    public ResponseEntity<Order> ConfirmedOrder(@PathVariable Long orderId) {
        Order order = this.orderService.confirmedOrder(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Order> canceledOrder(@PathVariable Long orderId) {
        Order order = this.orderService.canceledOrder(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @DeleteMapping("/{orderId}/delete")
    public ResponseEntity<ApiResponse> deleteOrder(@PathVariable Long orderId) {
        this.orderService.deletedOrder(orderId);
        String deleteSuccess = messageSource.getMessage("api.response.delete.order", null,
                LocaleContextHolder.getLocale());
        ApiResponse response = new ApiResponse();
        response.setMessage(deleteSuccess);
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
