package com.book_app_apis.application.payloads.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class AuthenticationResponse {
    private String access_token;
    private String email;
    private String statusCode;
    private int statusCodeValue;
}
