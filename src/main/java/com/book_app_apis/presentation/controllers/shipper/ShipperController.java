package com.book_app_apis.presentation.controllers.shipper;

import com.book_app_apis.domain.entities.Order;
import com.book_app_apis.domain.entities.User;
import com.book_app_apis.domain.exceptions.UserException;
import com.book_app_apis.domain.service.OrderService;
import com.book_app_apis.domain.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/api/shipper")
public class ShipperController {

    private final OrderService orderService;
    private final UserService userService;

    public ShipperController(OrderService orderService, UserService userService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @PutMapping("/{orderId}/ship")
    public ResponseEntity<Order> shippedOrder(@PathVariable Long orderId,
                                              @RequestHeader("Authorization") String jwt) throws UserException {
        User shipper = this.userService.getProfileUser(jwt);
        Order order = this.orderService.shippedOrder(orderId, shipper.getUser_id());
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PutMapping("/{orderId}/deliver")
    public ResponseEntity<Order> deliveredOrder(@PathVariable Long orderId,
                                                @RequestHeader("Authorization") String jwt) throws UserException {
        User shipper = this.userService.getProfileUser(jwt);
        Order order = this.orderService.deliveredOrder(orderId, shipper.getUser_id());
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PutMapping("/{orderId}/success")
    public ResponseEntity<Order> deliveredOrderSuccess(@PathVariable Long orderId,
                                                @RequestHeader("Authorization") String jwt) throws UserException {
        User shipper = this.userService.getProfileUser(jwt);
        Order order = this.orderService.successDelivery(orderId, shipper.getUser_id());
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}
