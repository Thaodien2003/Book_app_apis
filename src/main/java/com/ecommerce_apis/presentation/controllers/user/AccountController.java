package com.ecommerce_apis.presentation.controllers.user;

import com.ecommerce_apis.domain.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) {
        try {
            accountService.forgotPassword(email);
            return ResponseEntity.ok("Please check your email to set a new password.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/regenerate-otp")
    public ResponseEntity<String> regenerateOtp(@RequestParam("email") String email) {
        try {
            accountService.regenerateOtp(email);
            return ResponseEntity.ok("Check the OTP code sent to your email.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/set-password")
    public ResponseEntity<String> changePassword(@RequestParam("email") String email,
                                                 @RequestHeader("otp") String otp,
                                                 @RequestHeader("newPassword") String newPassword) {
        try {
            String response = accountService.changePassword(email, otp, newPassword);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
