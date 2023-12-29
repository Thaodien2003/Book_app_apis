package com.book_app_apis.application.serviceimpl;

import com.book_app_apis.domain.entities.Role;
import com.book_app_apis.domain.entities.User;
import com.book_app_apis.domain.exceptions.UserException;
import com.book_app_apis.domain.service.UserService;
import com.book_app_apis.infrastructure.gateways.UserMapper;
import com.book_app_apis.infrastructure.repositories.RoleRepository;
import com.book_app_apis.infrastructure.repositories.UserRepository;
import com.book_app_apis.presentation.dtos.UserDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;

    private final UserMapper userMapper;
    private final MessageSource messageSource;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder, JwtService jwtService,
                           UserMapper userMapper,
                           MessageSource messageSource) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.messageSource = messageSource;
    }

    @SuppressWarnings("unused")
    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(new HashSet<>());
        String userLogInfo = messageSource.getMessage("user.saved.log.info", null,
                LocaleContextHolder.getLocale());
        logger.info(userLogInfo + " - " + user.getEmail());
        return userRepository.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        String roleLogInfo = messageSource.getMessage("user.role.log.info", null,
                LocaleContextHolder.getLocale());
        logger.info(roleLogInfo + " - " + role.getName());
        return roleRepository.save(role);
    }

    @Override
    public void addToUser(String username, String rolename) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            logger.warn("User not found with username - " + username);
            throw new IllegalArgumentException("User with email " + username + " does not exist");
        }

        Role role = roleRepository.findByName(rolename);
        if (role == null) {
            String roleLogWarn = messageSource.getMessage("user.add.log.warn", null,
                    LocaleContextHolder.getLocale());
            String roleErrorRuntime = messageSource.getMessage("user.add.error.runtime", null,
                    LocaleContextHolder.getLocale());
            logger.warn(roleLogWarn + " - " + rolename);
            throw new IllegalArgumentException(roleErrorRuntime + " - " + rolename);
        }

        user.getRoles().add(role);
    }

    @Override
    public User findUserById(String userId) throws UserException {

        Optional<User> user = this.userRepository.findById(userId);

        if (user.isPresent()) {
            return user.get();
        }
        String userNotFound = messageSource.getMessage("user.not.found.id", null,
                LocaleContextHolder.getLocale());
        logger.warn(userNotFound + " - " + userId);
        throw new UserException(userNotFound + " - " + userId);
    }

    @Override
    public User getProfileUser(String jwt) throws UserException {

        String email = this.jwtService.getEmailFromToken(jwt);

        User user = this.userRepository.findByEmail(email);

        if (user == null) {
            String emailNotFound = messageSource.getMessage("user.not.found.email", null,
                    LocaleContextHolder.getLocale());
            logger.warn(emailNotFound + " - " + email);
            throw new UserException(emailNotFound + " - " + email);
        }
        String profileLogInfo = messageSource.getMessage("user.profile.log.info", null,
                LocaleContextHolder.getLocale());
        logger.info(profileLogInfo);
        return user;
    }

    @Override
    @Transactional
    public UserDTO updateUser(UserDTO userDTO, String userId) throws UserException {
        User existingUser = findUserById(userId);
        if (existingUser == null) {
            String userNotFound = messageSource.getMessage("user.not.found.id", null,
                    LocaleContextHolder.getLocale());
            logger.warn(userNotFound + " - " + userId);
            throw new UserException(userNotFound + " - " + userId);
        }

        if (!existingUser.isDeleted()) {
            existingUser.setUsername(userDTO.getUsername());
            existingUser.setEmail(userDTO.getEmail());
            existingUser.setUpdatedAt(LocalDateTime.now());

            String updateInfo = messageSource.getMessage("user.update.log.info", null,
                    LocaleContextHolder.getLocale());
            User updatedUser = this.userRepository.save(existingUser);
            logger.info(updateInfo + " - " + updatedUser);
            return this.userMapper.convertToDTO(updatedUser);
        } else {
            String updateLogError = messageSource.getMessage("user.update.log.error", null,
                    LocaleContextHolder.getLocale());
            String errorRuntime = messageSource.getMessage("user.update.error.runtime", null,
                    LocaleContextHolder.getLocale());
            logger.error(updateLogError);
            throw new UserException(errorRuntime);
        }
    }

    @Override
    @Transactional
    public void deletedUser(String userId) throws UserException {
        User existingUser = findUserById(userId);
        if (existingUser == null) {
            throw new UserException("User not found with id - " + userId);
        }

        existingUser.setDeleted(true);
        existingUser.setDeletedTime(LocalDateTime.now());
        logger.info("Before save: " + existingUser.getDeletedTime());
        this.userRepository.save(existingUser);
    }

    @SuppressWarnings("CallToPrintStackTrace")
    @Scheduled(fixedRate = 7 * 24 * 60 * 60 * 1000)
    public void deleteInactiveUsers() {
        LocalDateTime currentDate = LocalDateTime.now();

        List<User> inactiveUsers = userRepository.findInactiveUsers(currentDate);
        for (User inactiveUser : inactiveUsers) {
            try {
                userRepository.delete(inactiveUser);
                logger.info("Deleted account - " + inactiveUser);
            } catch (Exception e) {
                logger.error("Delete account user failed");
                e.printStackTrace();
            }
        }
    }
}
