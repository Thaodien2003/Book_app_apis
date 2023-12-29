package com.book_app_apis.infrastructure.repositories;

import com.book_app_apis.domain.entities.Token;
import com.book_app_apis.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    Token findByUser(User user);
    @Query("SELECT t FROM Token t WHERE t.refresh_token = :refreshToken")
    Token findByRefreshToken(@Param("refreshToken") String refreshToken);
}
