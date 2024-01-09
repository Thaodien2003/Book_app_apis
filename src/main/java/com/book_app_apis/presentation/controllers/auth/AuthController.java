package com.book_app_apis.presentation.controllers.auth;

import com.book_app_apis.application.payloads.request.AuthenticationRequest;
import com.book_app_apis.application.payloads.request.RegisterRequest;
import com.book_app_apis.application.serviceimpl.AuthenticationService;
import com.book_app_apis.presentation.dtos.TokenDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    //register account user
    @SuppressWarnings("deprecation")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authenticationService.register(registerRequest));
    }

    //login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<?> refreshToken(@RequestBody TokenDTO tokenDTO) {
        return ResponseEntity.ok(authenticationService.refreshToken(tokenDTO));
    }
}
