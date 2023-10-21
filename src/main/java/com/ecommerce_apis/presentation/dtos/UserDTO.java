package com.ecommerce_apis.presentation.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserDTO {
    private String user_id;
    private String username;
    private String email;
    private String avartar;
}
