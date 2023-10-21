package com.ecommerce_apis.presentation.controllers.user;

import com.ecommerce_apis.application.payloads.request.CartItemRequest;
import com.ecommerce_apis.application.payloads.response.ApiResponse;
import com.ecommerce_apis.domain.entities.Cart;
import com.ecommerce_apis.domain.entities.CartItem;
import com.ecommerce_apis.domain.service.CartItemService;
import com.ecommerce_apis.domain.service.CartService;
import com.ecommerce_apis.infrastructure.gateways.CartMapper;
import com.ecommerce_apis.presentation.dtos.CartDTO;
import com.ecommerce_apis.presentation.dtos.UserDTO;
import com.ecommerce_apis.domain.entities.User;
import com.ecommerce_apis.domain.exceptions.UserException;
import com.ecommerce_apis.domain.repositories.UserRepository;
import com.ecommerce_apis.domain.service.FileService;
import com.ecommerce_apis.infrastructure.gateways.UserMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import com.ecommerce_apis.domain.service.UserService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

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

    @Value("${project.image}")
    private String path;

    @Autowired
    public UserController(UserService userService, FileService fileService,
                          UserRepository userRepository,
                          UserMapper userMapper,
                          CartService cartService,
                          CartItemService cartItemService,
                          CartMapper cartMapper) {
        this.userService = userService;
        this.fileService = fileService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.cartService = cartService;
        this.cartItemService = cartItemService;
        this.cartMapper = cartMapper;
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
}
