package com.book_app_apis.presentation.controllers.auth;

import com.book_app_apis.application.payloads.request.AuthenticationRequest;
import com.book_app_apis.application.payloads.request.RegisterRequest;
import com.book_app_apis.application.serviceimpl.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final MessageSource messageSource;

    public AuthController(AuthenticationService authenticationService, MessageSource messageSource) {
        this.authenticationService = authenticationService;
        this.messageSource = messageSource;
    }

    //register account user
    @SuppressWarnings("deprecation")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            ResponseEntity<?> response = authenticationService.register(registerRequest);
            if (response.getStatusCode().is2xxSuccessful()) {
                String successMessage = messageSource.getMessage("registration.user.successful",
                        null, LocaleContextHolder.getLocale());
                return ResponseEntity.ok(successMessage);
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

    //register account shippper
    @SuppressWarnings("deprecation")
    @PostMapping("/register/shipper")
    public ResponseEntity<?> registerSeller(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            ResponseEntity<?> response = authenticationService.registerSeller(registerRequest);
            if (response.getStatusCode().is2xxSuccessful()) {
                String successMessage = messageSource.getMessage("registration.shipper.successful",
                        null, LocaleContextHolder.getLocale());
                return ResponseEntity.ok(successMessage);
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
    public ResponseEntity<?> login(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }

}
