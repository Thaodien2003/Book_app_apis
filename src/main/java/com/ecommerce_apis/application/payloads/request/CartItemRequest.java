package com.ecommerce_apis.application.payloads.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {

    private Long productId;

    private String size;

    private int quantity;

    private int price;
}
