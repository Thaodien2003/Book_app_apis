package com.book_app_apis.domain.service;

import com.book_app_apis.domain.entities.Token;
import com.book_app_apis.domain.entities.User;

public interface TokenService {
    Token addToken(User user, String token, String refresh_token);
}
