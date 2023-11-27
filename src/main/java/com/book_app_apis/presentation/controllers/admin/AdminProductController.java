package com.book_app_apis.presentation.controllers.admin;

import com.book_app_apis.application.payloads.response.ApiResponse;
import com.book_app_apis.domain.service.FileService;
import com.book_app_apis.domain.service.ProductService;
import com.book_app_apis.presentation.dtos.ProductDTO;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin")
public class AdminProductController {
    private final ProductService productService;
    private final FileService fileService;
    private final MessageSource messageSource;

    public AdminProductController(ProductService productService, FileService fileService, MessageSource messageSource) {
        this.productService = productService;
        this.fileService = fileService;
        this.messageSource=messageSource;
    }

    //seller create product
    @PostMapping("/category/{categoryId}/products")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO, @PathVariable Long categoryId) {
        ProductDTO createProduct = this.productService.createProduct(productDTO, categoryId);
        return new ResponseEntity<>(createProduct, HttpStatus.CREATED);
    }

    //upload image product
    @PostMapping("/product/image/upload/{productId}")
    public ResponseEntity<ProductDTO> uploadImage(@RequestParam("image") MultipartFile image,
                                                  @PathVariable Long productId) throws IOException {
        ProductDTO productDTO = this.productService.findProductById(productId);
        String imageUrl = this.fileService.uploadImage(image);

        productDTO.setImageUrl(imageUrl);

        ProductDTO update = this.productService.updateImageProduct(productDTO, productId);
        return new ResponseEntity<>(update, HttpStatus.OK);
    }

    //seller update product
    @PutMapping("/product/update/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody ProductDTO productDTO,
                                                    @PathVariable Long productId) {
        ProductDTO updateProduct = this.productService.updateProduct(productDTO, productId);
        return new ResponseEntity<>(updateProduct, HttpStatus.CREATED);
    }

    //seller delete product
    @DeleteMapping("/product/delete/{productId}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId) {
        this.productService.deleteProduct(productId);
        String deleteSucees = messageSource.getMessage("api.response.delete.product", null,
                LocaleContextHolder.getLocale());
        return new ResponseEntity<>(new ApiResponse(deleteSucees, true), HttpStatus.OK);
    }
}
