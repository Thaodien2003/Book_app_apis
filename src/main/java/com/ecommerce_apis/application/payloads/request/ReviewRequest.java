package com.ecommerce_apis.application.payloads.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    @NotEmpty
    private Long productId;

    @NotEmpty
    private String review;
}
