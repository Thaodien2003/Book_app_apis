package com.ecommerce_apis.application.payloads.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class RegisterRequest {
	
	private String user_name;
	
	private String password;
	
	private String email;
	
	private String mobile;

}
