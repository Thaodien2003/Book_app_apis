package com.ecommerce_apis.application.serviceimpl;

import com.ecommerce_apis.domain.service.AccountService;
import com.ecommerce_apis.application.utils.EmailUtil;
import com.ecommerce_apis.application.utils.OtpUtil;
import com.ecommerce_apis.domain.entities.User;
import com.ecommerce_apis.infrastructure.repositories.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class AccountImpl implements AccountService {

    private final UserRepository userRepository;
    private final EmailUtil emailUtil;
    private final OtpUtil otpUtil;
    private final PasswordEncoder passwordEncoder;

    public AccountImpl(UserRepository userRepository,
                       EmailUtil emailUtil,
                       PasswordEncoder passwordEncoder,
                       OtpUtil otpUtil) {
        this.userRepository = userRepository;
        this.emailUtil = emailUtil;
        this.passwordEncoder = passwordEncoder;
        this.otpUtil = otpUtil;
    }

    @Override
    public void forgotPassword(String email) {
        User user = this.userRepository.findByEmail(email);
        if(user == null) {
            throw new RuntimeException("User not found with email - "+email);
        }
        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendSetPassword(email, otp);
            user.setOtp(otp);
            user.setOtpGeneratedTime(LocalDateTime.now());
            userRepository.save(user);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send set password email please try again");
        }

    }

    @Override
    public String changePassword(String email, String otp, String newPassword) {
        User user = this.userRepository.findByEmail(email);
        if(user == null) {
            throw new RuntimeException("User not found with email - "+email);
        }
        if(user.getOtp().equals(otp) &&
                Duration.between(user.getOtpGeneratedTime(), LocalDateTime.now()).getSeconds() < 60) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            return "Change password successfully please login with new password";
        }

        return "Invalid or expired OTP. Please request a new OTP.";
    }

    @Override
    public void regenerateOtp(String email) {
        User user = this.userRepository.findByEmail(email);
        if(user == null) {
            throw new RuntimeException("User not found with email - "+email);
        }
        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendSetPassword(email, otp);
            user.setOtp(otp);
            user.setOtpGeneratedTime(LocalDateTime.now());
            userRepository.save(user);
        } catch (MessagingException e) {
            throw new RuntimeException("An error occurred while sending the otp code, please try again");
        }

    }
}
