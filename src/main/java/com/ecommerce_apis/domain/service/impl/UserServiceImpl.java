package com.ecommerce_apis.domain.service.impl;

import com.ecommerce_apis.domain.entities.Cart;
import com.ecommerce_apis.domain.service.CartService;
import com.ecommerce_apis.presentation.dtos.UserDTO;
import com.ecommerce_apis.domain.service.UserService;
import com.ecommerce_apis.domain.entities.Role;
import com.ecommerce_apis.domain.entities.User;
import com.ecommerce_apis.domain.exceptions.UserException;
import com.ecommerce_apis.domain.repositories.RoleRepository;
import com.ecommerce_apis.domain.repositories.UserRepository;
import com.ecommerce_apis.infrastructure.gateways.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        return userRepository.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void addToUser(String username, String rolename) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new IllegalArgumentException("User with email " + username + " does not exist");
        }

        Role role = roleRepository.findByName(rolename);
        if (role == null) {
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

        throw new UserException("User not found with id - " + userId);
    }

    @Override
    public User getProfileUser(String jwt) throws UserException {

        String email = this.jwtService.getEmailFromToken(jwt);

        User user = this.userRepository.findByEmail(email);

        if (user == null) {
            throw new UserException("User not found with email - " + email);
        }

        return user;
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO, String userId) throws UserException {
        User existingUser = findUserById(userId);
        if(existingUser == null) {
            throw new UserException("User not found with id - " + userId);
        }

        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setAvartar(userDTO.getAvartar());
        existingUser.setUpdatedAt(LocalDateTime.now());

        User update = this.userRepository.save(existingUser);

        return this.userMapper.convertToDTO(update);
    }

}
