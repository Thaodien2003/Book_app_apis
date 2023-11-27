package com.book_app_apis.application.serviceimpl;

import com.book_app_apis.domain.service.AccountService;
import com.book_app_apis.application.utils.EmailUtil;
import com.book_app_apis.application.utils.OtpUtil;
import com.book_app_apis.domain.entities.User;
import com.book_app_apis.infrastructure.repositories.UserRepository;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
    private final MessageSource messageSource;
    private static final Logger logger = LoggerFactory.getLogger(AccountImpl.class);

    public AccountImpl(UserRepository userRepository,
                       EmailUtil emailUtil,
                       PasswordEncoder passwordEncoder,
                       OtpUtil otpUtil,
                       MessageSource messageSource) {
        this.userRepository = userRepository;
        this.emailUtil = emailUtil;
        this.passwordEncoder = passwordEncoder;
        this.otpUtil = otpUtil;
        this.messageSource = messageSource;
    }

    @Override
    public void forgotPassword(String email) {
        User user = this.userRepository.findByEmail(email);
        if (user == null) {
            String userNotFound = messageSource.getMessage("account.user.notfound.email", null,
                    LocaleContextHolder.getLocale());
            logger.error(userNotFound + " - " + email);
            throw new RuntimeException(userNotFound + " - " + email);
        }
        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendEmail(email, otp);
            user.setOtp(passwordEncoder.encode(otp));
            user.setOtpGeneratedTime(LocalDateTime.now());
            userRepository.save(user);
        } catch (MessagingException e) {
            String unableSend = messageSource.getMessage("account.unable.send", null,
                    LocaleContextHolder.getLocale());
            String sendTry = messageSource.getMessage("account.try.send", null,
                    LocaleContextHolder.getLocale());
            logger.error(unableSend + " - " + email, e);
            throw new RuntimeException(sendTry);
        }
    }

    @Override
    public String changePassword(String email, String otp, String newPassword) {
        User user = this.userRepository.findByEmail(email);
        if (user == null) {
            String userNotFound = messageSource.getMessage("account.user.notfound.email", null,
                    LocaleContextHolder.getLocale());
            logger.error(userNotFound + " - " + email);
            throw new RuntimeException(userNotFound + " - " + email);
        }

        if (passwordEncoder.matches(otp, user.getOtp()) && !user.isDeleted() &&
                Duration.between(user.getOtpGeneratedTime(), LocalDateTime.now()).getSeconds() < 60) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());
            String changePass = messageSource.getMessage("account.changePass.log.info", null,
                    LocaleContextHolder.getLocale());
            String changPassSuccess = messageSource.getMessage("account.changePass.success", null,
                    LocaleContextHolder.getLocale());
            userRepository.save(user);
            logger.info(changePass + " - " + email);
            return changPassSuccess;
        }
        String exPiredOtp = messageSource.getMessage("account.expired.otp.log.error", null,
                LocaleContextHolder.getLocale());
        String returnExpiredOtp = messageSource.getMessage("account.expired.otp.return", null,
                LocaleContextHolder.getLocale());
        logger.error(exPiredOtp + " - " + email);
        return returnExpiredOtp;
    }

    @Override
    public void regenerateOtp(String email) {
        User user = this.userRepository.findByEmail(email);
        if (user == null) {
            String userNotFound = messageSource.getMessage("account.user.notfound.email", null,
                    LocaleContextHolder.getLocale());
            logger.error(userNotFound + " - " + email);
            throw new RuntimeException(userNotFound + " - " + email);
        }
        String otp = otpUtil.generateOtp();
        try {
            String sendOtpLogInfo = messageSource.getMessage("account.send.otp.log.info", null,
                    LocaleContextHolder.getLocale());
            emailUtil.sendEmail(email, otp);
            logger.info(sendOtpLogInfo + " - " + email);
            user.setOtp(passwordEncoder.encode(otp));
            user.setOtpGeneratedTime(LocalDateTime.now());
            userRepository.save(user);
        } catch (MessagingException e) {
            String sendOtpLogError = messageSource.getMessage("account.send.otp.log.error", null,
                    LocaleContextHolder.getLocale());
            String errorSendOtp = messageSource.getMessage("account.send.otp.error", null,
                    LocaleContextHolder.getLocale());
            logger.error(sendOtpLogError + " - " + email, e);
            throw new RuntimeException(errorSendOtp);
        }

    }

    @Override
    public String recoverAccount(String email, String otp) {
        try {
            User user = this.userRepository.findByEmail(email);
            if (user == null) {
                String userNotFound = messageSource.getMessage("account.user.notfound.email", null,
                        LocaleContextHolder.getLocale());
                logger.error(userNotFound + " - " + email);
                throw new RuntimeException(userNotFound + " - " + email);
            }

            if (passwordEncoder.matches(otp, user.getOtp()) && user.isDeleted() &&
                    Duration.between(user.getOtpGeneratedTime(), LocalDateTime.now()).getSeconds() < 60 &&
                    Duration.between(user.getDeletedTime(), LocalDateTime.now()).toDays() < 20) {
                user.setDeleted(false);
                userRepository.save(user);
                return messageSource.getMessage("account.recover", null,
                        LocaleContextHolder.getLocale());
            }

            String expiredOtpLog = messageSource.getMessage("account.expired.otp.log.error", null,
                    LocaleContextHolder.getLocale());
            String sendOtpReturn = messageSource.getMessage("account.expired.otp.return", null,
                    LocaleContextHolder.getLocale());
            logger.warn(expiredOtpLog + " - " + email);
            return sendOtpReturn;
        } catch (Exception e) {
            String recoverLogError = messageSource.getMessage("account.recover.log.error", null,
                    LocaleContextHolder.getLocale());
            String errorRuntime = messageSource.getMessage("account.error.rutime", null,
                    LocaleContextHolder.getLocale());
            logger.error(recoverLogError + "-" + email, e);
            throw new RuntimeException(errorRuntime, e);
        }
    }

}
