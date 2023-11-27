package com.book_app_apis.domain.service;

import com.book_app_apis.presentation.dtos.ProductDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface ProductRedisService {
    void clear();
    List<ProductDTO> getAllProducts(String cacheKey)
            throws JsonProcessingException;
    void saveAllProducts(List<ProductDTO> productDTOS, String cacheKey)
            throws JsonProcessingException;
}
