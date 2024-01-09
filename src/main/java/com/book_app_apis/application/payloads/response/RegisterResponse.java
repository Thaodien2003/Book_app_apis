package com.book_app_apis.application.payloads.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor@NoArgsConstructor
public class RegisterResponse {
    private String status;
    private String message;
}
