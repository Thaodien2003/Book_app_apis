package com.ecommerce_apis.application.serviceimpl;

import com.ecommerce_apis.presentation.dtos.UserDTO;
import com.ecommerce_apis.domain.service.UserService;
import com.ecommerce_apis.domain.entities.Role;
import com.ecommerce_apis.domain.entities.User;
import com.ecommerce_apis.domain.exceptions.UserException;
import com.ecommerce_apis.infrastructure.repositories.RoleRepository;
import com.ecommerce_apis.infrastructure.repositories.UserRepository;
import com.ecommerce_apis.infrastructure.gateways.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
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
    private static final Logger logger = Logger.getLogger(UserServiceImpl.class);


    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder, JwtService jwtService,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    @SuppressWarnings("unused")
    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(new HashSet<>());
        logger.info("Save user successfully with user - " + user.getEmail());
        return userRepository.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        logger.info("Save role successfully with - " + role.getName());
        return roleRepository.save(role);
    }

    @Override
    public void addToUser(String username, String rolename) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            logger.warn("User not found with username - "+username);
            throw new IllegalArgumentException("User with email " + username + " does not exist");
        }

        Role role = roleRepository.findByName(rolename);
        if (role == null) {
            logger.warn("Role not found with rolename - "+rolename);
            throw new IllegalArgumentException("Role with name " + rolename + " does not exist");
        }

        user.getRoles().add(role);
    }

    @Override
    public User findUserById(String userId) throws UserException {

        Optional<User> user = this.userRepository.findById(userId);

        if (user.isPresent()) {
            return user.get();
        }
        logger.warn("User not found with id - "+userId);
        throw new UserException("User not found with id - " + userId);
    }

    @Override
    public User getProfileUser(String jwt) throws UserException {

        String email = this.jwtService.getEmailFromToken(jwt);

        User user = this.userRepository.findByEmail(email);

        if (user == null) {
            logger.warn("User not found with email - "+email);
            throw new UserException("User not found with email - " + email);
        }
        logger.info("Profile user");
        return user;
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO, String userId) throws UserException {
        User existingUser = findUserById(userId);
        if (existingUser == null) {
            logger.warn("User not found");
            throw new UserException("User not found with id - " + userId);
        }

        if (!existingUser.isDeleted()) {
            existingUser.setUsername(userDTO.getUsername());
            existingUser.setEmail(userDTO.getEmail());
            existingUser.setUpdatedAt(LocalDateTime.now());

            User updatedUser = this.userRepository.save(existingUser);
            logger.info("Update user successfully - "+updatedUser);
            return this.userMapper.convertToDTO(updatedUser);
        } else {
            // Xử lý khi user đã bị đánh dấu xóa
            logger.error("Update user failed");
            throw new UserException("Cannot update a deleted user.");
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    @Override
    public void deletedUser(String userId) throws UserException {
        User existingUser = findUserById(userId);
        if (existingUser == null) {
            throw new UserException("User not found with id - " + userId);
        }

        existingUser.setDeleted(true);
        existingUser.setDeletedTime(LocalDateTime.now());
        this.userRepository.save(existingUser);

        // Log thời điểm trước và sau khi lưu vào cơ sở dữ liệu
        logger.info("Before save: " + existingUser.getDeletedTime());
        this.userRepository.save(existingUser);
        logger.info("After save: " + existingUser.getDeletedTime());

        LocalDateTime deletionDate = existingUser.getDeletedTime().plusMinutes(2);
        LocalDateTime currentDate = LocalDateTime.now();
        logger.info("Delete day: " + deletionDate);
        logger.info("Now: " + currentDate);

        if (Duration.between(existingUser.getDeletedTime(), currentDate).toMinutes() > 2) {
            // Log khi thực hiện xóa
            logger.info("Delete account - "+userId);
            try {
                this.userRepository.delete(existingUser);
            } catch (Exception e) {
                // Log lỗi nếu có
                logger.error("Delete account user failed");
                e.printStackTrace();
            }
        }
    }
}
