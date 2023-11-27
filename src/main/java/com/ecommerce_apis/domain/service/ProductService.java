package com.ecommerce_apis.domain.service;

import com.ecommerce_apis.domain.exceptions.UserException;
import com.ecommerce_apis.presentation.dtos.ProductDTO;
import com.ecommerce_apis.application.payloads.response.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    //create product
    ProductDTO createProduct(ProductDTO productDTO, Long categoryId, String sellerId) throws UserException;

    //update product
    ProductDTO updateProduct(ProductDTO productDTO, Long productId, String sellerId) throws UserException;

    //delete
    void deleteProduct(Long productId, String sellerId) throws UserException;

    //find product by id
    ProductDTO findProductById(Long productId);

    //find product by category
    List<ProductDTO> findProductByCategory(Long categoryId);

    //get all product
   ProductResponse getAllProduct(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

    List<ProductDTO> findAllProducts();

    //search product
    List<ProductDTO> searchProduct(String keyword);

    //upadte image product
    ProductDTO updateImageProduct(ProductDTO productDTO, Long productId, String sellerId) throws UserException;

    //filter product
    Page<ProductDTO> getFilterProduct(
            String category, List<String> colors, List<String> sizes,
            Integer minPrice, Integer maxPrice, Integer minDiscount,
            String sortDir, String stock, Integer pageNumber, Integer pageSize);

    //get all product by seller id
    List<ProductDTO> findAllProductsBySeller(String sellerId) throws UserException;
}
