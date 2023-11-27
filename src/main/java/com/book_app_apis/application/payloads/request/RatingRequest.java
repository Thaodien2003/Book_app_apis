package com.book_app_apis.application.payloads.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequest {
    @NotEmpty
    private Long productId;

    @NotEmpty
    private double rating;
}
