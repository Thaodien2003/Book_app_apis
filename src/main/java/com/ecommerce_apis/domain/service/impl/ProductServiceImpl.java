package com.ecommerce_apis.domain.service.impl;

import com.ecommerce_apis.presentation.dtos.ProductDTO;
import com.ecommerce_apis.application.payloads.response.ProductResponse;
import com.ecommerce_apis.domain.service.ProductService;
import com.ecommerce_apis.domain.entities.Category;
import com.ecommerce_apis.domain.entities.Product;
import com.ecommerce_apis.domain.exceptions.ResourceNotFoundException;
import com.ecommerce_apis.domain.repositories.CategoryRepository;
import com.ecommerce_apis.domain.repositories.ProductRepository;
import com.ecommerce_apis.infrastructure.gateways.ProductMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO, Long categoryId) {
        Category category = this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Category id", categoryId));

        Product product = this.productMapper.convertToEntity(productDTO);
        product.setTitle(productDTO.getTitle());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setDiscountedPrice(productDTO.getDiscountedPrice());
        product.setDiscountPersent(productDTO.getDiscountedPersent());
        product.setQuantity(productDTO.getQuantity());
        product.setBrand(productDTO.getBrand());
        product.setColor(productDTO.getColor());
        product.setSizes(productDTO.getSize());
        product.setCategory(category);
        product.setCreatedAt(LocalDateTime.now());

        Product addProduct = this.productRepository.save(product);

        return this.productMapper.convertToDTO(addProduct);
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "Product Id", productId));

        product.setTitle(productDTO.getTitle());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setDiscountedPrice(productDTO.getDiscountedPrice());
        product.setDiscountPersent(productDTO.getDiscountedPersent());
        product.setQuantity(productDTO.getQuantity());
        product.setBrand(productDTO.getBrand());
        product.setColor(productDTO.getColor());
        product.setSizes(productDTO.getSize());
        product.setUpdateAt(LocalDateTime.now());

        Product updateProduct = this.productRepository.save(product);

        return this.productMapper.convertToDTO(updateProduct);
    }

    @Override
    public void deleteProduct(Long productId) {
        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "Product Id", productId));
        this.productRepository.delete(product);
    }

    @Override
    public ProductDTO findProductById(Long productId) {
        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "Product Id", productId));

        return this.productMapper.convertToDTO(product);
    }

    @Override
    public List<ProductDTO> findProductByCategory(Long categoryId) {
        Category category = this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "category id",
                        categoryId));
        List<Product> products = this.productRepository.findProductByCategory(category);

        return products.stream().map(this.productMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getAllProduct(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable page = PageRequest.of(pageNumber, pageSize, sort);

        Page<Product> pageProduct = this.productRepository.findAll(page);

        List<Product> allProducts = pageProduct.getContent();
        List<ProductDTO> productDTOs = allProducts.stream().map(this.productMapper::convertToDTO)
                .collect(Collectors.toList());

        ProductResponse productResponse = new ProductResponse();

        productResponse.setProducts(productDTOs);
        productResponse.setPageNumber(pageProduct.getNumber());
        productResponse.setPageSize(pageProduct.getSize());
        productResponse.setTotalElements(pageProduct.getTotalElements());
        productResponse.setTotalPages(pageProduct.getTotalPages());
        productResponse.setLastPage(pageProduct.isLast());

        return productResponse;
    }

    @Override
    public List<ProductDTO> findAllProducts() {
        return null;
    }

    @Override
    public List<ProductDTO> searchProduct(String keyword) {
        List<Product> products = this.productRepository.findByTitle("%" + keyword + "%");
        return products.stream().map(this.productMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO updateImageProduct(ProductDTO productDTO, Long productId) {
        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "Product Id", productId));

        product.setImageUrl(productDTO.getImageUrl());
        Product updateImageProduct = this.productRepository.save(product);

        return this.productMapper.convertToDTO(updateImageProduct);
    }
}
