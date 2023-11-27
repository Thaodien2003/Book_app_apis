package com.ecommerce_apis.presentation.controllers.inventory;

import com.ecommerce_apis.domain.service.CategoryService;
import com.ecommerce_apis.presentation.dtos.CategoryDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    //get single category
    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable Long catId) {
        CategoryDTO getCategoryDTO = this.categoryService.getCategoryId(catId);
        return new ResponseEntity<>(getCategoryDTO, HttpStatus.OK);
    }

    //get all category
    @GetMapping("/")
    public ResponseEntity<List<CategoryDTO>> getCategories() {
        List<CategoryDTO> categories = this.categoryService.getCategories();
        return ResponseEntity.ok(categories);
    }
}
