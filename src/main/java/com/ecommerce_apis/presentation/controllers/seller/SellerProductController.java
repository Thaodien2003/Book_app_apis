package com.ecommerce_apis.presentation.controllers.seller;

import com.ecommerce_apis.application.payloads.response.ApiResponse;
import com.ecommerce_apis.domain.entities.User;
import com.ecommerce_apis.domain.exceptions.UserException;
import com.ecommerce_apis.domain.service.FileService;
import com.ecommerce_apis.domain.service.ProductService;
import com.ecommerce_apis.domain.service.UserService;
import com.ecommerce_apis.presentation.dtos.ProductDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/seller")
public class SellerProductController {

    private final ProductService productService;
    private final FileService fileService;
    private final UserService userService;

    @Value("${project.image}")
    private String path;

    public SellerProductController(ProductService productService, FileService fileService,
                                   UserService userService) {
        this.productService = productService;
        this.fileService = fileService;
        this.userService = userService;
    }

    //seller create product
    @PostMapping("/category/{categoryId}/products")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO, @PathVariable Long categoryId,
                                                    @RequestHeader("Authorization") String jwt) throws UserException {

        User seller = this.userService.getProfileUser(jwt);
        ProductDTO createProduct = this.productService.createProduct(productDTO, categoryId, seller.getUser_id());
        return new ResponseEntity<>(createProduct, HttpStatus.CREATED);
    }

    //upload image product
    @PostMapping("/product/image/upload/{productId}")
    public ResponseEntity<ProductDTO> uploadImage(@RequestParam("image") MultipartFile image,
                                                  @PathVariable Long productId,
                                                  @RequestHeader("Authorization") String jwt) throws UserException,
            IOException {
        User seller = this.userService.getProfileUser(jwt);
        ProductDTO productDTO = this.productService.findProductById(productId);
        String fileName = this.fileService.uploadImage(path, image);

        productDTO.setImageUrl(fileName);

        ProductDTO update = this.productService.updateImageProduct(productDTO, productId, seller.getUser_id());
        return new ResponseEntity<>(update, HttpStatus.OK);
    }

    //seller update product
    @PutMapping("/product/update/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody ProductDTO productDTO,
                                                    @PathVariable Long productId,
                                                    @RequestHeader("Authorization") String jwt) throws UserException {
        User seller = this.userService.getProfileUser(jwt);
        ProductDTO updateProduct = this.productService.updateProduct(productDTO, productId, seller.getUser_id());
        return new ResponseEntity<>(updateProduct, HttpStatus.CREATED);
    }

    //seller delete product
    @DeleteMapping("/product/delete/{productId}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId,
                                                     @RequestHeader("Authorization") String jwt) throws UserException {

        User seller = this.userService.getProfileUser(jwt);
        this.productService.deleteProduct(productId, seller.getUser_id());
        return new ResponseEntity<>(new ApiResponse("Product is Deleted Successfully", true), HttpStatus.OK);
    }

    @GetMapping("/product/")
    public ResponseEntity<List<ProductDTO>> getProductBySeller(@RequestHeader("Authorization") String jwt) throws UserException {

        User seller = this.userService.getProfileUser(jwt);
        List<ProductDTO> products = this.productService.findAllProductsBySeller(seller.getUser_id());
        return ResponseEntity.ok(products);
    }
}
