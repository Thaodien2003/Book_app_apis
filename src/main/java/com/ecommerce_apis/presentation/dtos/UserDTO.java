package com.ecommerce_apis.presentation.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserDTO {
    private String user_id;

    @NotEmpty
    @Size(min = 5, max = 30, message = "Username must be between 4 and 30 characters !!")
    private String username;

    @NotEmpty
    @Pattern(regexp = "\\b[A-Z0-9._%-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b", message = "Invalid email format")
    private String email;

    private String avartar;
}
