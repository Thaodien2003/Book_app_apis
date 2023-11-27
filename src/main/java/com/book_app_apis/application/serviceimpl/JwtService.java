package com.book_app_apis.application.serviceimpl;

import com.book_app_apis.domain.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private static final String secretKey = "2206200367822799272372000228828277362729181819282227272";
    private final MessageSource messageSource;

    SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

    public JwtService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String generateToken(User user, Collection<SimpleGrantedAuthority> authorities) {
        try {
            return Jwts.builder()
                    .setSubject(user.getEmail())
                    .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                    .claim("roles", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                    .signWith(key).compact();
        } catch (Exception e) {
            String generateLogError = messageSource.getMessage("jwt.generate.log.error", null,
                    LocaleContextHolder.getLocale());
            logger.error(generateLogError + "-" + e.getMessage());
            throw e;
        }
    }

    public String getEmailFromToken(String jwt) {
        try {
            jwt = jwt.substring(7);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            return claims.getSubject();
        } catch (Exception e) {
            String extractLogError = messageSource.getMessage("jwt.extract.log.error", null,
                    LocaleContextHolder.getLocale());
            logger.error(extractLogError + "-" + e.getMessage());
            throw e;
        }
    }
}
