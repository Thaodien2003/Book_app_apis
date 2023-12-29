package com.book_app_apis.application.serviceimpl;

import com.book_app_apis.domain.entities.Token;
import com.book_app_apis.domain.entities.User;
import com.book_app_apis.domain.service.TokenService;
import com.book_app_apis.infrastructure.repositories.TokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(TokenServiceImpl.class);
    private final MessageSource messageSource;

    public TokenServiceImpl(TokenRepository tokenRepository, MessageSource messageSource) {
        this.tokenRepository = tokenRepository;
        this.messageSource = messageSource;
    }

    @Override
    public Token addToken(User user, String token, String refresh_token) {
        Token existingToken = tokenRepository.findByUser(user);
        if (existingToken != null) {
            existingToken.setRefresh_token(refresh_token);
            existingToken.setToken(token);
            existingToken.setRevoked(false);
            existingToken.setRefersh_token_expired(LocalDateTime.now().plusHours(24));
            existingToken.setExpired_token(LocalDateTime.now().plusHours(12));
            String infoUpdate = messageSource.getMessage("token.info.update", null, LocaleContextHolder.getLocale());
            logger.info(infoUpdate + " - " + user);
            return tokenRepository.save(existingToken);
        } else {
            Token newToken = new Token();
            newToken.setToken(token);
            newToken.setRefresh_token(refresh_token);
            newToken.setExpired_token(LocalDateTime.now().plusHours(12));
            newToken.setRefersh_token_expired(LocalDateTime.now().plusHours(24));
            newToken.setRevoked(false);
            newToken.setUser(user);
            String logInfo = messageSource.getMessage("token.log.info", null, LocaleContextHolder.getLocale());
            logger.info(logInfo + " - " + user);
            return tokenRepository.save(newToken);
        }
    }
}
