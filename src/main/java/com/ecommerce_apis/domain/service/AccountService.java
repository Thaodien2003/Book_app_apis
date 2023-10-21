package com.ecommerce_apis.domain.service;

public interface AccountService {
    String forgotPassword(String email);
    String changePassword(String email, String otp, String newPassword);
    String regenerateOtp(String email);
}
