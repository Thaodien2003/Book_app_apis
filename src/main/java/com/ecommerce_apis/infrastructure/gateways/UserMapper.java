package com.ecommerce_apis.infrastructure.gateways;

import com.ecommerce_apis.presentation.dtos.UserDTO;
import com.ecommerce_apis.domain.entities.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private final ModelMapper modelMapper;

    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
    public UserDTO convertToDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
    public User convertToEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }
}
