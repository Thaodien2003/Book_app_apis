package com.book_app_apis.domain.service;

import com.book_app_apis.presentation.dtos.CategoryDTO;

import java.util.List;

public interface CategoryService {
    // create new category
    CategoryDTO createCategory(CategoryDTO categoryDTO);

    //update category
    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);

    //delete category
    void deleteCategory(Long categoryId);

    //get category by id
    CategoryDTO getCategoryId(Long categoryId);

    //get all category
    List<CategoryDTO> getCategories();

    //update image Category
    CategoryDTO updateImageCategory(CategoryDTO categoryDTO, Long categoryId);
}
