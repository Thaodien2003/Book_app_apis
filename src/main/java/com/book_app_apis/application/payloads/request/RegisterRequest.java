package com.book_app_apis.application.payloads.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class RegisterRequest {

	@NotEmpty
	@Size(min = 5, max = 30, message = "Username must be between 4 and 30 characters !!")
	private String user_name;

	@NotEmpty
	@Size(min = 10, max=30, message = "Password must be between 10 and 30 characters!")
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"|,.<>/?]).{10,30}$")
	private String password;

	@Getter
	@NotEmpty
	private String email;

	@NotEmpty
	private String mobile;

}
