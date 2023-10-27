package com.ecommerce_apis.presentation.controllers.auth;

import com.ecommerce_apis.presentation.dtos.UserDTO;
import com.ecommerce_apis.domain.entities.User;
import com.ecommerce_apis.infrastructure.gateways.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce_apis.application.payloads.request.AuthenticationRequest;
import com.ecommerce_apis.application.payloads.request.RegisterRequest;
import com.ecommerce_apis.domain.service.impl.AuthenticationService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;

    public AuthController(AuthenticationService authenticationService, UserMapper userMapper) {
        this.authenticationService = authenticationService;
        this.userMapper = userMapper;
    }

    //register account user
    @SuppressWarnings("deprecation")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            ResponseEntity<?> response = authenticationService.register(registerRequest);
            if (response.getStatusCode().is2xxSuccessful()) {
                User user = (User) response.getBody();
                UserDTO userDTO = userMapper.convertToDTO(user);
                return ResponseEntity.ok(userDTO);
            } else {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                String errorMessage = Objects.requireNonNull(response.getBody()).toString();

                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("body", errorMessage);
                errorResponse.put("statusCode", response.getStatusCode().toString());
                errorResponse.put("statusCodeValue", response.getStatusCodeValue());

                String jsonString = objectMapper.writeValueAsString(errorResponse);

                return ResponseEntity.status(response.getStatusCode())
                        .headers(headers)
                        .body(jsonString);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }
}
