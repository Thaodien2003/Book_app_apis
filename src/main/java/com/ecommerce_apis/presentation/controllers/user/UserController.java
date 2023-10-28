package com.ecommerce_apis.presentation.controllers.user;

import com.ecommerce_apis.application.payloads.request.CartItemRequest;
import com.ecommerce_apis.application.payloads.request.RatingRequest;
import com.ecommerce_apis.application.payloads.request.ReviewRequest;
import com.ecommerce_apis.application.payloads.response.ApiResponse;
import com.ecommerce_apis.domain.entities.*;
import com.ecommerce_apis.domain.exceptions.UserException;
import com.ecommerce_apis.infrastructure.repositories.UserRepository;
import com.ecommerce_apis.domain.service.*;
import com.ecommerce_apis.infrastructure.gateways.CartMapper;
import com.ecommerce_apis.infrastructure.gateways.UserMapper;
import com.ecommerce_apis.presentation.dtos.CartDTO;
import com.ecommerce_apis.presentation.dtos.UserDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final FileService fileService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CartService cartService;
    private final CartItemService cartItemService;
    private final CartMapper cartMapper;
    private final OrderService orderService;
    private final RatingService ratingService;
    private final ReviewService reviewService;

    @Value("${project.image}")
    private String path;

    public UserController(UserService userService, FileService fileService,
                          UserRepository userRepository,
                          UserMapper userMapper,
                          CartService cartService,
                          CartItemService cartItemService,
                          CartMapper cartMapper,
                          OrderService orderService,
                          RatingService ratingService,
                          ReviewService reviewService) {
        this.userService = userService;
        this.fileService = fileService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.cartService = cartService;
        this.cartItemService = cartItemService;
        this.cartMapper = cartMapper;
        this.orderService = orderService;
        this.ratingService = ratingService;
        this.reviewService = reviewService;
    }

    //get profile user
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String jwt) throws UserException {
        User user = userService.getProfileUser(jwt);
        UserDTO userDTO = this.userMapper.convertToDTO(user);
        return new ResponseEntity<>(userDTO, HttpStatus.ACCEPTED);
    }

    //user upload avartar
    @PostMapping("/avartar/upload/")
    public ResponseEntity<?> addAvartarUser(
            @RequestHeader("Authorization") String jwt,
            @RequestParam("avartar") MultipartFile avartar) throws IOException, UserException {

        User user = this.userService.getProfileUser(jwt);
        UserDTO userDTO = null;
        if (user != null) {
            String fileName = this.fileService.uploadImage(path, avartar);
            user.setAvartar(fileName);
            user.setUpdatedAt(LocalDateTime.now());
            this.userRepository.save(user);
            userDTO = this.userMapper.convertToDTO(user);
        }

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    // method to server files
    @GetMapping(value = "/avartar/{avartarName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public void downloadImage(
            @PathVariable("avartarName") String avartarName,
            HttpServletResponse response) throws IOException {
        InputStream resource = this.fileService.getResource(path, avartarName);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());
    }

    //update user
    @PutMapping("/update/{userId}")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO,
                                              @PathVariable String userId) throws UserException {
        UserDTO update = this.userService.updateUser(userDTO, userId);
        return new ResponseEntity<>(update, HttpStatus.OK);
    }

    //find cart by user id
    @GetMapping("/cart/")
    public ResponseEntity<CartDTO> findCart(@RequestHeader("Authorization") String jwt) throws UserException {
        User user = userService.getProfileUser(jwt);
        Cart cart = cartService.findUserCart(user.getUser_id());
        CartDTO cartDTO = this.cartMapper.convertToDTO(cart);

        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    //add item to cart
    @PostMapping("/cart/add")
    public ResponseEntity<ApiResponse> addItemToCart(@RequestBody CartItemRequest req,
                                                     @RequestHeader("Authorization") String jwt) throws UserException {

        User user = userService.getProfileUser(jwt);
        this.cartService.addCartItem(user.getUser_id(), req);

        ApiResponse res = new ApiResponse();
        res.setMessage("item add to cart");
        res.setSuccess(true);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    //user update cart
    @PutMapping("/cart/update/{cartItemId}")
    public ResponseEntity<CartItem> updateCartItem(@RequestBody CartItem cartItem,
                                                   @PathVariable Long cartItemId,
                                                   @RequestHeader("Authorization") String jwt) throws UserException {

        User user = this.userService.getProfileUser(jwt);
        CartItem updateCartItem = this.cartItemService.updateCartItem(user.getUser_id(), cartItemId, cartItem);

        return new ResponseEntity<>(updateCartItem, HttpStatus.OK);
    }

    //user detele cart
    @DeleteMapping("/cart/delete/{cartItemId}")
    public ResponseEntity<ApiResponse> deletedCartItem(@PathVariable Long cartItemId,
                                                       @RequestHeader("Authorization") String jwt) throws UserException {

        User user = this.userService.getProfileUser(jwt);

        this.cartItemService.removedCartItem(user.getUser_id(), cartItemId);

        ApiResponse res = new ApiResponse();
        res.setMessage("delete item from cart");
        res.setSuccess(true);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    //user createOrder
    @PostMapping("/order/create-order")
    public ResponseEntity<Order> createdOrder(@RequestBody Address shippingAddress,
                                              @RequestHeader("Authorization") String jwt) throws UserException {
        User user = this.userService.getProfileUser(jwt);
        Order order = this.orderService.createOrder(user, shippingAddress);
        System.out.println("order : " + order);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    //user placed order
    @SuppressWarnings("unused")
    @PostMapping("/order/{orderId}/placed")
    public ResponseEntity<Order> placesOrder(@PathVariable Long orderId,
                                             @RequestHeader("Authorization") String jwt) throws UserException {
        User user = this.userService.getProfileUser(jwt);
        Order order = this.orderService.placeOrder(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    //View user's order history
    @GetMapping("/order/history")
    public ResponseEntity<List<Order>> userOrderHistory(@RequestHeader("Authorization") String jwt) throws UserException {
        User user = this.userService.getProfileUser(jwt);

        List<Order> orders = this.orderService.userOrderHistory(user.getUser_id());

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    //get order by id
    @GetMapping("/order/{id}")
    public ResponseEntity<Order> findOrderById(@PathVariable("id") Long orderId) {
        Order order = this.orderService.findOrderById(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    //user rating product
    @PostMapping("/ratings/create-rating")
    public ResponseEntity<Rating> createdRating(@RequestBody RatingRequest request,
                                                @RequestHeader("Authorization") String jwt) throws UserException {
        User user = this.userService.getProfileUser(jwt);
        Rating rating = this.ratingService.createdRating(request, user);
        return new ResponseEntity<>(rating, HttpStatus.CREATED);
    }

    //get product rating

    @GetMapping("/ratings/product/{productId}")
    public ResponseEntity<List<Rating>> getProductsRating(@PathVariable Long productId) {
        List<Rating> rating = this.ratingService.getProductRating(productId);
        return new ResponseEntity<>(rating, HttpStatus.OK);
    }

    //user review product
    @PostMapping("/review/create-review")
    public ResponseEntity<Review> createdReview(@RequestBody ReviewRequest request,
                                                @RequestHeader("Authorization") String jwt) throws UserException {
        User user = this.userService.getProfileUser(jwt);
        Review review = this.reviewService.createdReview(request, user);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }


    @GetMapping("/review/product/{productId}")
    public ResponseEntity<List<Review>> getProductsReview(@PathVariable Long productId) {
        List<Review> reviews = this.reviewService.getAllReview(productId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

}
