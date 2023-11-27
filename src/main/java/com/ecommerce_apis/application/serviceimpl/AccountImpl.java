package com.ecommerce_apis.application.serviceimpl;

import com.ecommerce_apis.domain.service.AccountService;
import com.ecommerce_apis.application.utils.EmailUtil;
import com.ecommerce_apis.application.utils.OtpUtil;
import com.ecommerce_apis.domain.entities.User;
import com.ecommerce_apis.infrastructure.repositories.UserRepository;
import jakarta.mail.MessagingException;
import org.apache.log4j.Logger;
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
    private static final Logger logger = Logger.getLogger(AccountImpl.class);

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
        if (user == null) {
            logger.error("User not found with email - " + email);
            throw new RuntimeException("User not found with email - " + email);
        }
        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendEmail(email, otp);
            user.setOtp(passwordEncoder.encode(otp));
            user.setOtpGeneratedTime(LocalDateTime.now());
            userRepository.save(user);
        } catch (MessagingException e) {
            logger.error("Unable to send set password email for user - " + email, e);
            throw new RuntimeException("Unable to send set password email please try again");
        }
    }

    @Override
    public String changePassword(String email, String otp, String newPassword) {
        User user = this.userRepository.findByEmail(email);
        if (user == null) {
            logger.error("User not found with email - " + email);
            throw new RuntimeException("User not found with email - " + email);
        }

        if (passwordEncoder.matches(otp, user.getOtp()) && !user.isDeleted() &&
                Duration.between(user.getOtpGeneratedTime(), LocalDateTime.now()).getSeconds() < 60) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            logger.info("Change password successfully with email - " + email);
            return "Change password successfully please login with new password";
        }
        logger.error("Invalid or expired OTP with email - " + email);
        return "Invalid or expired OTP. Please request a new OTP.";
    }

    @Override
    public void regenerateOtp(String email) {
        User user = this.userRepository.findByEmail(email);
        if (user == null) {
            logger.error("User not found with email - " + email);
            throw new RuntimeException("User not found with email - " + email);
        }
        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendEmail(email, otp);
            logger.info("Send OTP with email - " + email);
            user.setOtp(passwordEncoder.encode(otp));
            user.setOtpGeneratedTime(LocalDateTime.now());
            userRepository.save(user);
        } catch (MessagingException e) {
            logger.error("An error occurred while sending the otp code with - " + email, e);
            throw new RuntimeException("An error occurred while sending the otp code, please try again");
        }

    }

    @Override
    public String recoverAccount(String email, String otp) {
        try {
            User user = this.userRepository.findByEmail(email);
            if (user == null) {
                logger.error("User not found with email - " + email);
                throw new RuntimeException("User not found with email - " + email);
            }

            if (passwordEncoder.matches(otp, user.getOtp()) && user.isDeleted() &&
                    Duration.between(user.getOtpGeneratedTime(), LocalDateTime.now()).getSeconds() < 60 &&
                    Duration.between(user.getDeletedTime(), LocalDateTime.now()).toMinutes() < 2) {
                user.setDeleted(false);
                userRepository.save(user);
                return "Recover account successfully please login";
            }

            logger.warn("Invalid or expired OTP for user - " + email);
            return "Invalid or expired OTP. Please request a new OTP.";
        } catch (Exception e) {
            logger.error("Error during account recovery for user - " + email, e);
            throw new RuntimeException("An error occurred during account recovery, please try again", e);
        }
    }

}
