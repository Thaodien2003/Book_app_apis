package com.ecommerce_apis.domain.service;

import com.ecommerce_apis.domain.entities.Role;
import com.ecommerce_apis.domain.entities.User;
import com.ecommerce_apis.domain.exceptions.UserException;
import com.ecommerce_apis.domain.service.UserService;
import com.ecommerce_apis.infrastructure.repositories.RoleRepository;
import com.ecommerce_apis.infrastructure.repositories.UserRepository;
import com.ecommerce_apis.presentation.dtos.UserDTO;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User user;

    private Role role;

    @BeforeEach
    public void setUp() {

        //data user
        user = new User();
        user.setUsername("Test123");
        user.setEmail("test@example.com");
        user.setPassword("test12345");

        //data role
        role = new Role();
        role.setName("ROLE_TEST");
        role.setDescription("This is test");
    }

    @AfterEach
    public void tearDown() {
        //delete data user test
        if (user != null && user.getUser_id() != null) {
            userRepository.deleteById(user.getUser_id());
        }

        //delete data role test
        if(role!=null && role.getId()!=null) {
            roleRepository.deleteById(role.getId());
        }
    }

    @Test
    public void testSaveUser() {
        User saveUser = userService.saveUser(user);
        assertNotNull(saveUser.getUser_id());
    }

    @Test
    public void testSaveRole() {
        Role saveRole = userService.saveRole(role);
        assertNotNull(saveRole.getId());
    }

    @Test
    public void testFindUserById_success() throws UserException {
        String userId = userRepository.save(user).getUser_id();

        User foundUser = userService.findUserById(userId);

        assertEquals(foundUser.getUser_id(), userId);
    }

    @Test
    public void testFindUserById_notFound() {
        String invalidUserId = "invalid-id";

        assertThrows(Exception.class, () -> userService.findUserById(invalidUserId));
    }

    @Test
    public void testUpdateUser_success() throws UserException {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newUsername");
        userDTO.setEmail("newEmail");

        userRepository.save(user);
        String userId = user.getUser_id();

        UserDTO updatedUser = userService.updateUser(userDTO, userId);

        User updatedUserInDB = userRepository.findById(userId).orElse(null);

        assert updatedUserInDB != null;
        assertEquals("newUsername", updatedUserInDB.getUsername());
        assertEquals("newEmail", updatedUserInDB.getEmail());

        assertEquals("newUsername", updatedUser.getUsername());
        assertEquals("newEmail", updatedUser.getEmail());
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        String userId = "nonExistentUserId";
        UserDTO userDTO = new UserDTO();

        assertThrows(UserException.class, () -> userService.updateUser(userDTO, userId));
    }

    @Transactional
    @Test
    public void testAddToUser_UserAndRoleExist() {
        roleRepository.save(role);

        userRepository.save(user);

        userService.addToUser(user.getEmail(), role.getName());

        String emailUser = user.getEmail();
        User user = userRepository.findByEmail(emailUser);

        String roleName = role.getName();
        Role role = roleRepository.findByName(roleName);

        Hibernate.initialize(user.getRoles());
        assertTrue(user.getRoles().contains(role));
    }

    @Test
    void testAddToUser_UserNotFound() {
        assertThrows(IllegalArgumentException.class, () ->
                userService.addToUser("nonexistent@example.com", "ROLE_TEST"));
    }

    @Test
    void testAddToUser_RoleNotFound() {
        assertThrows(IllegalArgumentException.class, () ->
                userService.addToUser("test@example.com", "NON_EXISTENT_ROLE"));
    }
}
