package com.ecommerce_apis.presentation.dtos;

import com.ecommerce_apis.domain.entities.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long productId;
    private String title;
    private String description;
    private int price;
    private int discountedPrice;
    private int discountedPersent;
    private int quantity;
    private String brand;
    private String color;
    private Set<Size> size = new HashSet<>();
    private String imageUrl;
    private CategoryDTO category;
}
