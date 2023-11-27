package com.ecommerce_apis.presentation.controllers.seller;

import com.ecommerce_apis.application.payloads.response.ApiResponse;
import com.ecommerce_apis.domain.entities.Order;
import com.ecommerce_apis.domain.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller/orders")
public class SellerOrderController {

    private final OrderService orderService;

    public SellerOrderController(OrderService orderService) {
        this.orderService = orderService;
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
        ApiResponse response = new ApiResponse();
        response.setMessage("Order deleted successfully");
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
