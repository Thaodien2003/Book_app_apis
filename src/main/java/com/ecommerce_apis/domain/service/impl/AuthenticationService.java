package com.ecommerce_apis.domain.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import com.ecommerce_apis.domain.entities.Cart;
import com.ecommerce_apis.domain.service.CartService;
import com.ecommerce_apis.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce_apis.domain.entities.Role;
import com.ecommerce_apis.domain.entities.User;
import com.ecommerce_apis.application.payloads.request.AuthenticationRequest;
import com.ecommerce_apis.application.payloads.response.AuthenticationResponse;
import com.ecommerce_apis.application.payloads.request.RegisterRequest;
import com.ecommerce_apis.domain.repositories.RoleCustomRepo;
import com.ecommerce_apis.domain.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private UserRepository userRepository;

    private JwtService jwtService;

    private AuthenticationManager authenticationManager;

    private UserService userService;

    private RoleCustomRepo roleCustomRepo;

    private final CartService cartService;

    @Autowired
    public AuthenticationService(UserRepository userRepository,
                                 JwtService jwtService,
                                 AuthenticationManager authenticationManager,
                                 UserService userService,
                                 RoleCustomRepo roleCustomRepo,
                                 CartService cartService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.roleCustomRepo = roleCustomRepo;
        this.cartService = cartService;
    }

    public ResponseEntity<?> register(RegisterRequest registerRequest) {
        try {
            if (userRepository.findByEmail(registerRequest.getEmail()) != null) {
                throw new IllegalArgumentException("User with email " + registerRequest.getEmail() + " already exists");
            }

            User user = new User(
                    registerRequest.getUser_name(),
                    registerRequest.getPassword(),
                    registerRequest.getEmail(),
                    registerRequest.getMobile(),
                    new HashSet<>(),
                    LocalDateTime.now()
            );
            userService.saveUser(user);

            Cart cart = cartService.createCart(user);

            userService.addToUser(registerRequest.getEmail(), "ROLE_USER"); // Default role

            User savedUser = userRepository.findByEmail(registerRequest.getEmail());

            return ResponseEntity.ok(savedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> authenticate(AuthenticationRequest authenticationRequest) {
        try {
            User user = userRepository.findByEmail(authenticationRequest.getEmail());
            if (user == null) {
                throw new NoSuchElementException("User not found");
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));

            List<Role> roles = roleCustomRepo.getRole(user.getEmail());

            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            Set<Role> roleSet = new HashSet<>();

            for (Role role : roles) {
                roleSet.add(new Role(role.getName()));
                authorities.add(new SimpleGrantedAuthority(role.getName()));
            }

            user.setRoles(roleSet);

            for (Role role : roleSet) {
                authorities.add(new SimpleGrantedAuthority(role.getName()));
            }

            String jwtAccessToken = jwtService.generateToken(user, authorities);
            String jwtRefreshToken = jwtService.generateRefreshToken(user, authorities);

            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .access_token(jwtAccessToken)
                    .refresh_token(jwtRefreshToken)
                    .email(user.getEmail())
                    .build());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Invalid Credential");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }


}
