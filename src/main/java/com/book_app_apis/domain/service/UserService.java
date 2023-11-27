package com.book_app_apis.domain.service;

import com.book_app_apis.presentation.dtos.UserDTO;
import com.book_app_apis.domain.entities.Role;
import com.book_app_apis.domain.entities.User;
import com.book_app_apis.domain.exceptions.UserException;

public interface UserService {
	User saveUser(User user);

	Role saveRole(Role role);

	void addToUser(String username,String rolename);
	
	User findUserById(String userId) throws UserException;
	
	User getProfileUser(String jwt) throws UserException;

	UserDTO updateUser(UserDTO userDTO, String userId) throws  UserException;

	void deletedUser(String userId) throws UserException;
}
