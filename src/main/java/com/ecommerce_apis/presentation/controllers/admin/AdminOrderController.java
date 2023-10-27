package com.ecommerce_apis.presentation.controllers.admin;

import com.ecommerce_apis.application.payloads.response.ApiResponse;
import com.ecommerce_apis.domain.entities.Order;
import com.ecommerce_apis.domain.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = this.orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PutMapping("/{orderId}/confirmed")
    public ResponseEntity<Order> ConfirmedOrder(@PathVariable Long orderId) {
        Order order =  this.orderService.confirmedOrder(orderId);

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PutMapping("/{orderId}/ship")
    public ResponseEntity<Order> shippedOrder(@PathVariable Long orderId) {

        Order order = this.orderService.shippedOrder(orderId);

        return new ResponseEntity<>(order, HttpStatus.OK);

    }

    @PutMapping("/{orderId}/deliver")
    public ResponseEntity<Order> deliveredOrder(@PathVariable Long orderId) {

        Order order = this.orderService.deliveredOrder(orderId);

        return new ResponseEntity<>(order, HttpStatus.OK);

    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Order> canceledOrder(@PathVariable Long orderId) {

        Order order = this.orderService.canceledOrder(orderId);

        return new ResponseEntity<>(order, HttpStatus.OK);

    }

    @DeleteMapping ("/{orderId}/delete")
    public ResponseEntity<ApiResponse> deleteOrder(@PathVariable Long orderId) {

        this.orderService.deletedOrder(orderId);

        ApiResponse response = new ApiResponse();
        response.setMessage("Order deleted successfully");
        response.setSuccess(true);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
