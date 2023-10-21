package com.ecommerce_apis.presentation.controllers.admin;

import com.ecommerce_apis.application.payloads.response.ApiResponse;
import com.ecommerce_apis.domain.service.FileService;
import com.ecommerce_apis.domain.service.ProductService;
import com.ecommerce_apis.presentation.dtos.ProductDTO;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${project.image}")
    private String path;

    public AdminProductController(ProductService productService, FileService fileService) {
        this.productService = productService;
        this.fileService = fileService;
    }
	
	@GetMapping("/url")
    public ResponseEntity<?> admin(){
        return ResponseEntity.ok("This is Admin Route");
    }

    //admin create product
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
        String fileName = this.fileService.uploadImage(path, image);

        productDTO.setImageUrl(fileName);

        ProductDTO update = this.productService.updateImageProduct(productDTO, productId);

        return new ResponseEntity<>(update, HttpStatus.OK);
    }

    //admin update product
    @PutMapping("/product/update/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody ProductDTO productDTO,
                                                    @PathVariable Long productId) {
        ProductDTO updateProduct = this.productService.updateProduct(productDTO, productId);
        return new ResponseEntity<>(updateProduct, HttpStatus.CREATED);
    }

    //admin delete product
    @DeleteMapping("/product/delete/{productId}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId) {
        try {
            this.productService.deleteProduct(productId);
            return new ResponseEntity<>(new
                    ApiResponse("Product is Deleted Successfully", true),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Product is Deleted Failed", false),
                    HttpStatus.OK);
        }
    }
}
