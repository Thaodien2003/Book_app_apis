package com.ecommerce_apis.presentation.controllers.admin;

import com.ecommerce_apis.application.payloads.response.ApiResponse;
import com.ecommerce_apis.domain.service.CategoryService;
import com.ecommerce_apis.domain.service.FileService;
import com.ecommerce_apis.presentation.dtos.CategoryDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @Value("${project.image}")
    private String path;

    private final FileService fileService;

    public AdminCategoryController(CategoryService categoryService,
                                   FileService fileService) {
        this.categoryService = categoryService;
        this.fileService = fileService;
    }

    //admin create categoty
    @PostMapping("/categories/create")
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO createCategory = this.categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(createCategory, HttpStatus.CREATED);
    }

    //admin update category
    @PutMapping("/categories/update/{catId}")
    public ResponseEntity<CategoryDTO> updateCategoryDTOResponseEntity(@RequestBody CategoryDTO categoryDTO,
                                                                       @PathVariable Long catId) {

            CategoryDTO updateCategory = this.categoryService.updateCategory(categoryDTO, catId);
            return new ResponseEntity<>(updateCategory, HttpStatus.OK);
    }

    //upload image category
    @PostMapping("/categories/image/upload/{catId}")
    public ResponseEntity<CategoryDTO> uploadImage(@RequestParam("image") MultipartFile image,
                                                   @PathVariable Long catId) throws IOException {
        CategoryDTO categoryDTO = this.categoryService.getCategoryId(catId);
        String fileName = this.fileService.uploadImage(path, image);

        categoryDTO.setCategoryImage(fileName);

        CategoryDTO update = this.categoryService.updateImageCategory(categoryDTO, catId);
        return new ResponseEntity<>(update, HttpStatus.OK);
    }

    //admin delete category
    @DeleteMapping("/categories/delete/{catId}")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long catId) {
            this.categoryService.deleteCategory(catId);
            return new ResponseEntity<>(new
                    ApiResponse("Category is Deleted Successfully", true),
                    HttpStatus.OK);
    }
}
