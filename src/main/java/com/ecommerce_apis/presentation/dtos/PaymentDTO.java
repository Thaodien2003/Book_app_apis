package com.ecommerce_apis.presentation.dtos;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class PaymentDTO implements Serializable {
    private String status;
    private String message;
    private String Url;
}
