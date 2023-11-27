package com.book_app_apis.presentation.controllers.admin;

import com.book_app_apis.application.payloads.response.ApiResponse;
import com.book_app_apis.domain.service.CategoryService;
import com.book_app_apis.domain.service.FileService;
import com.book_app_apis.presentation.dtos.CategoryDTO;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin")
public class AdminCategoryController {

    private final CategoryService categoryService;
    private final MessageSource messageSource;

    private final FileService fileService;

    public AdminCategoryController(CategoryService categoryService,
                                   FileService fileService,
                                   MessageSource messageSource) {
        this.categoryService = categoryService;
        this.fileService = fileService;
        this.messageSource = messageSource;
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
        String fileName = this.fileService.uploadImage(image);

        categoryDTO.setCategoryImage(fileName);

        CategoryDTO update = this.categoryService.updateImageCategory(categoryDTO, catId);
        return new ResponseEntity<>(update, HttpStatus.OK);
    }

    //admin delete category
    @DeleteMapping("/categories/delete/{catId}")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long catId) {
            this.categoryService.deleteCategory(catId);
            String deleteSuccess = messageSource.getMessage("api.respone.delete.category", null,
                    LocaleContextHolder.getLocale());
            return new ResponseEntity<>(new
                    ApiResponse(deleteSuccess, true),
                    HttpStatus.OK);
    }
}
