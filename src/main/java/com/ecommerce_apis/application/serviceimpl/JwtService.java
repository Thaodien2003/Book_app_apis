package com.ecommerce_apis.application.serviceimpl;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import io.jsonwebtoken.ExpiredJwtException;
import org.apache.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.ecommerce_apis.domain.entities.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final Logger logger = Logger.getLogger(JwtService.class);
    public static final String secretKey = "2206200367822799272372000228828277362729181819282227272";
    SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

    public String generateToken(User user, Collection<SimpleGrantedAuthority> authorities) {
        try {
            return Jwts.builder()
                    .setSubject(user.getEmail())
                    .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                    .claim("roles", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                    .signWith(key).compact();
        } catch (Exception e) {
            logger.error("Failed to generate token: " + e.getMessage());
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
            logger.error("Failed to extract email from token: " + e.getMessage());
            throw e;
        }
    }
}
