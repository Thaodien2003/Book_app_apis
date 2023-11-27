package com.book_app_apis.application.payloads.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AuthenticationRequest {
  @Getter
  @NotEmpty
  private String email;

  @NotEmpty
  @Size(min = 10, max=30, message = "Password must be between 10 and 30 characters!")
  @Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"|,.<>/?]).{10,30}$")
  private String password;

}
