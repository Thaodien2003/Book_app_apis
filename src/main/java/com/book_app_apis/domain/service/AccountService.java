package com.book_app_apis.domain.service;

public interface AccountService {
    void forgotPassword(String email);
    String changePassword(String email, String otp, String newPassword);
    void regenerateOtp(String email);
    String recoverAccount(String email, String otp);
}
