package com.ecommerce_apis.infrastructure.gateways;

import com.ecommerce_apis.domain.entities.Cart;
import com.ecommerce_apis.presentation.dtos.CartDTO;
import com.ecommerce_apis.presentation.dtos.UserDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CartMapper {
    private final ModelMapper modelMapper;

    public CartMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

    }
    public CartDTO convertToDTO(Cart cart) {
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cartDTO.setCartItems(cart.getCartItems());
        UserDTO userDTO = modelMapper.map(cart.getUser(), UserDTO.class);
        cartDTO.setUser(userDTO);
        return cartDTO;
    }
    public Cart convertToEntity(CartDTO cartDTO) {
        return modelMapper.map(cartDTO, Cart.class);
    }
}
