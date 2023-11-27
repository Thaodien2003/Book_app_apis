package com.book_app_apis.application.serviceimpl;

import com.book_app_apis.application.payloads.request.AuthenticationRequest;
import com.book_app_apis.application.payloads.request.RegisterRequest;
import com.book_app_apis.domain.entities.Role;
import com.book_app_apis.domain.entities.User;
import com.book_app_apis.domain.service.UserService;
import com.book_app_apis.infrastructure.repositories.RoleCustomRepo;
import com.book_app_apis.infrastructure.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final RoleCustomRepo roleCustomRepo;
    private final MessageSource messageSource;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    @Autowired
    public AuthenticationService(UserRepository userRepository,
                                 JwtService jwtService,
                                 AuthenticationManager authenticationManager,
                                 UserService userService,
                                 RoleCustomRepo roleCustomRepo,
                                 MessageSource messageSource) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.roleCustomRepo = roleCustomRepo;
        this.messageSource = messageSource;
    }

    //register account user
    public ResponseEntity<?> register(RegisterRequest registerRequest) {
        try {
            if (userRepository.findByEmail(registerRequest.getEmail()) != null) {
                String userExistsLog = messageSource.getMessage("registration.user.already.log.warn", null,
                        LocaleContextHolder.getLocale());
                String userExists = messageSource.getMessage("registration.user.already", null,
                        LocaleContextHolder.getLocale());
                logger.warn(userExistsLog);
                throw new IllegalArgumentException(userExists + registerRequest.getEmail());
            }

            User user = new User(
                    registerRequest.getUser_name(),
                    registerRequest.getPassword(),
                    registerRequest.getEmail(),
                    registerRequest.getMobile(),
                    new HashSet<>(),
                    false,
                    LocalDateTime.now()
            );
            userService.saveUser(user);
            userService.addToUser(registerRequest.getEmail(), "ROLE_USER"); // Default role

            String userSucessMessage = messageSource.getMessage("registration.user.successful", null,
                    LocaleContextHolder.getLocale());
            User savedUser = userRepository.findByEmail(registerRequest.getEmail());
            logger.info(userSucessMessage + " - " + savedUser);
            return ResponseEntity.ok(savedUser);
        } catch (IllegalArgumentException e) {
            String badRequest = messageSource.getMessage("request.bad.request.warn", null,
                    LocaleContextHolder.getLocale());
            logger.error(badRequest + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            String serverError = messageSource.getMessage("request.server.warn", null,
                    LocaleContextHolder.getLocale());
            logger.error(serverError + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //register account shipper
    public ResponseEntity<?> registerSeller(RegisterRequest registerRequest) {
        try {
            if (userRepository.findByEmail(registerRequest.getEmail()) != null) {
                String shipperExistsLog = messageSource.getMessage("registration.shipper.already.log.warn", null,
                        LocaleContextHolder.getLocale());
                String shipperExists = messageSource.getMessage("registration.shipper.already", null,
                        LocaleContextHolder.getLocale());
                logger.warn(shipperExistsLog);
                throw new IllegalArgumentException(shipperExists + registerRequest.getEmail());
            }

            User user = new User(
                    registerRequest.getUser_name(),
                    registerRequest.getPassword(),
                    registerRequest.getEmail(),
                    registerRequest.getMobile(),
                    new HashSet<>(),
                    false,
                    LocalDateTime.now()
            );
            userService.saveUser(user);

            userService.addToUser(registerRequest.getEmail(), "ROLE_SHIPPER"); // Default role
            String shipperSucessMessage = messageSource.getMessage("registration.shipper.successful", null,
                    LocaleContextHolder.getLocale());
            User savedUser = userRepository.findByEmail(registerRequest.getEmail());
            logger.info(shipperSucessMessage + " - " + savedUser);
            return ResponseEntity.ok(savedUser);
        } catch (IllegalArgumentException e) {
            String badRequest = messageSource.getMessage("request.bad.request.warn", null,
                    LocaleContextHolder.getLocale());
            logger.error(badRequest + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            String serverError = messageSource.getMessage("request.server.warn", null,
                    LocaleContextHolder.getLocale());
            logger.error(serverError + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public Map<String, Object> authenticate(AuthenticationRequest authenticationRequest) {
        try {
            User user = userRepository.findByEmail(authenticationRequest.getEmail());
            if (user == null) {
                String notFound = messageSource.getMessage("authenticate.notfound.warn", null,
                        LocaleContextHolder.getLocale());
                String userNotFound = messageSource.getMessage("authenticate.user.notfound", null,
                        LocaleContextHolder.getLocale());
                logger.warn(notFound + " - " + authenticationRequest.getEmail());
                throw new NoSuchElementException(userNotFound);
            }

            if (user.isDeleted()) {
                // User deleted, didn't login
                String userDelete = messageSource.getMessage("authenticate.user.delete.warn", null,
                        LocaleContextHolder.getLocale());
                logger.warn(userDelete + " - " + authenticationRequest.getEmail());
                String userNotLogin = messageSource.getMessage("authenticate.user.login", null,
                        LocaleContextHolder.getLocale());
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("statusCode", "FORBIDDEN");
                errorResponse.put("statusCodeValue", 403);
                errorResponse.put("error", userNotLogin);
                return errorResponse;
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
            logger.info("Authentication successful for user: " + user.getEmail());
            return getStringObjectMap(jwtAccessToken, user);
        } catch (NoSuchElementException e) {
            String logErrorAuthen = messageSource.getMessage("authenticate.log.error", null,
                    LocaleContextHolder.getLocale());
            logger.error(logErrorAuthen + "-" + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", "NOT_FOUND");
            errorResponse.put("statusCodeValue", 404);
            errorResponse.put("error", e.getMessage());
            return errorResponse;
        } catch (BadCredentialsException e) {
            logger.error("Authentication failed: Invalid credentials for user: " + authenticationRequest.getEmail());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", "BAD_REQUEST");
            errorResponse.put("statusCodeValue", 400);
            errorResponse.put("error", "Email Or Password is not correct");
            return errorResponse;
        } catch (Exception e) {
            String errSomething = messageSource.getMessage("authenticate.log.errorSomething", null,
                    LocaleContextHolder.getLocale());
            logger.error(errSomething + "-" + e.getMessage());
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
