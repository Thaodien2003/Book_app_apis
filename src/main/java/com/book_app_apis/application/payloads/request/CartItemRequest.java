package com.book_app_apis.application.payloads.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {

    @NotEmpty
    private Long productId;

    @NotEmpty
    private String size;

    @NotEmpty
    private int quantity;

    private int price;
}
