package com.ecommerce_apis.application.serviceimpl;

import com.ecommerce_apis.application.payloads.request.AuthenticationRequest;
import com.ecommerce_apis.application.payloads.request.RegisterRequest;
import com.ecommerce_apis.domain.entities.Role;
import com.ecommerce_apis.domain.entities.User;
import com.ecommerce_apis.infrastructure.repositories.RoleCustomRepo;
import com.ecommerce_apis.infrastructure.repositories.UserRepository;
import com.ecommerce_apis.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private UserRepository userRepository;

    private JwtService jwtService;

    private AuthenticationManager authenticationManager;

    private UserService userService;

    private RoleCustomRepo roleCustomRepo;


    @Autowired
    public AuthenticationService(UserRepository userRepository,
                                 JwtService jwtService,
                                 AuthenticationManager authenticationManager,
                                 UserService userService,
                                 RoleCustomRepo roleCustomRepo) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.roleCustomRepo = roleCustomRepo;
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

            userService.addToUser(registerRequest.getEmail(), "ROLE_USER"); // Default role

            User savedUser = userRepository.findByEmail(registerRequest.getEmail());

            return ResponseEntity.ok(savedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public Map<String, Object> authenticate(AuthenticationRequest authenticationRequest) {
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

            user.setToken(jwtAccessToken);
            userRepository.save(user);

            return getStringObjectMap(jwtAccessToken, user);
        } catch (NoSuchElementException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", "NOT_FOUND");
            errorResponse.put("statusCodeValue", 404);
            errorResponse.put("error", e.getMessage());
            return errorResponse;
        } catch (BadCredentialsException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", "BAD_REQUEST");
            errorResponse.put("statusCodeValue", 400);
            errorResponse.put("error", "Invalid Credential");
            return errorResponse;
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", "INTERNAL_SERVER_ERROR");
            errorResponse.put("statusCodeValue", 500);
            errorResponse.put("error", "Something went wrong");
            return errorResponse;
        }
    }

    private static Map<String, Object> getStringObjectMap(String jwtAccessToken, User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("access_token", jwtAccessToken);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userId", user.getUser_id());
        userMap.put("username", user.getUsername());
        userMap.put("avatar", user.getAvartar());
        userMap.put("email", user.getEmail());
        response.put("user", userMap);
        response.put("statusCode", "OK");
        response.put("statusCodeValue", 200);
        return response;
    }

}
