package com.book_app_apis.presentation.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CategoryDTO {
    private Long categoryId;

    private String categoryName;

    private String description;

    private String categoryImage;
}
