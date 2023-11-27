package com.book_app_apis.presentation.controllers.user;

import com.book_app_apis.domain.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;
    private final MessageSource messageSource;

    @Autowired
    public AccountController(AccountService accountService,
                             MessageSource messageSource) {
        this.accountService = accountService;
        this.messageSource = messageSource;
    }

    //forgot password
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) {
        String fotgotPass = messageSource.getMessage("account.controller.forgot", null,
                LocaleContextHolder.getLocale());
        accountService.forgotPassword(email);
        return ResponseEntity.ok(fotgotPass);
    }

    //generate otp
    @PostMapping("/regenerate-otp")
    public ResponseEntity<String> regenerateOtp(@RequestParam("email") String email) {
        String checkOtp = messageSource.getMessage("account.controller.otp", null,
                LocaleContextHolder.getLocale());
        accountService.regenerateOtp(email);
        return ResponseEntity.ok(checkOtp);
    }

    //set password
    @PostMapping("/set-password")
    public ResponseEntity<String> changePassword(@RequestParam("email") String email,
                                                 @RequestHeader("otp") String otp,
                                                 @RequestHeader("newPassword") String newPassword) {

        String response = accountService.changePassword(email, otp, newPassword);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/recover-account")
    public ResponseEntity<String> recoverAccount(@RequestParam("email") String email,
                                                 @RequestHeader("otp") String otp) {
        String response = accountService.recoverAccount(email, otp);
        return ResponseEntity.ok(response);
    }
}
