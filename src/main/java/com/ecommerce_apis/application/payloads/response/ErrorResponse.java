package com.ecommerce_apis.application.payloads.response;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private HttpStatus statusCode;
    private String errorMessage;
    private Object body;
    public ErrorResponse(HttpStatus statusCode, String errorMessage) {
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }

    public ErrorResponse(String errorMessage){
        this.errorMessage = errorMessage;
    }

    public int getStatusCodeValue(){
        return statusCode.value();
    }
}
