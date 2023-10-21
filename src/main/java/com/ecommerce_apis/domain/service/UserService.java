package com.ecommerce_apis.domain.service;

import com.ecommerce_apis.presentation.dtos.UserDTO;
import com.ecommerce_apis.domain.entities.Role;
import com.ecommerce_apis.domain.entities.User;
import com.ecommerce_apis.domain.exceptions.UserException;

public interface UserService {
	User saveUser(User user);

	Role saveRole(Role role);

	void addToUser(String username,String rolename);
	
	User findUserById(String userId) throws UserException;
	
	User getProfileUser(String jwt) throws UserException;

	UserDTO updateUser(UserDTO userDTO, String userId) throws  UserException;
}
