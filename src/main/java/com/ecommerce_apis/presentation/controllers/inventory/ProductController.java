package com.ecommerce_apis.presentation.controllers.inventory;

import com.ecommerce_apis.application.payloads.response.ProductResponse;
import com.ecommerce_apis.application.utils.Constants;
import com.ecommerce_apis.domain.service.ProductService;
import com.ecommerce_apis.presentation.dtos.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    //get all product
    @GetMapping("/")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(value = "pageNumber", defaultValue = Constants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = Constants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = Constants.SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = Constants.SORT_DIR, required = false) String sortDir) {
        ProductResponse allProduct = this.productService.getAllProduct(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(allProduct, HttpStatus.OK);
    }

    //filter product
    @GetMapping("/filter-product")
    public ResponseEntity<Page<ProductDTO>> getFilterProduct(
            @RequestParam String category,
            @RequestParam List<String> color, @RequestParam List<String> size, @RequestParam Integer minPrice,
            @RequestParam Integer maxPrice, @RequestParam Integer minDiscount, @RequestParam String stock,
            @RequestParam(value = "pageNumber", defaultValue = Constants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = Constants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam String sort
    ) {
        Page<ProductDTO> productDTOS = this.productService.getFilterProduct(category, color, size, minPrice, maxPrice,
                                                                            minDiscount, sort, stock, pageNumber, pageSize);
        System.out.println("Complete Product");
        return new ResponseEntity<>(productDTOS, HttpStatus.OK);
    }

    //search product
    @GetMapping("/search/{keywords}")
    public ResponseEntity<List<ProductDTO>> searchByTitle(
            @PathVariable("keywords") String keywords) {
        List<ProductDTO> resultSearch = this.productService.searchProduct(keywords);
        return new ResponseEntity<>(resultSearch, HttpStatus.OK);
    }

    //get product by category
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductByCategory(@PathVariable Long categoryId) {
        List<ProductDTO> products = this.productService.findProductByCategory(categoryId);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

}
