package com.ecommerce_apis.domain.service;

public interface AccountService {
    void forgotPassword(String email);
    String changePassword(String email, String otp, String newPassword);
    void regenerateOtp(String email);
}
