package com.ecommerce_apis.domain.service;

import com.ecommerce_apis.domain.entities.Category;
import com.ecommerce_apis.domain.entities.Product;
import com.ecommerce_apis.domain.exceptions.ResourceNotFoundException;
import com.ecommerce_apis.infrastructure.gateways.ProductMapper;
import com.ecommerce_apis.infrastructure.repositories.CategoryRepository;
import com.ecommerce_apis.infrastructure.repositories.ProductRepository;
import com.ecommerce_apis.presentation.dtos.ProductDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductMapper productMapper;

    private Category category;
    private Product product;
    private ProductDTO productDTO;
    Long product_id;
    Long category_id;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setName("Tai nghe bluetooth HYper red");
        category.setDescription("Color red, Jack 3.5mm");
        categoryRepository.save(category);
        category_id = category.getId();

        product = new Product();
        product.setTitle("Tai nghe bluetooth");
        product.setDescription("Tai nghe gaming");
        product.setPrice(200000);
        product.setDiscountedPrice(20000);
        product.setDiscountPersent(180000);
        product.setQuantity(100);
        product.setBrand("Hyber");
        product.setColor("Black");
        product.setCategory(category);
        productRepository.save(product);
        productDTO = productMapper.convertToDTO(product);
        product_id = product.getId();
    }

    @AfterEach
    void tearDown() {
        if(product != null && product_id != null) {
            productRepository.deleteById(product_id);
        }

        if(category != null && category_id != null) {
            categoryRepository.deleteById(category_id);
        }
    }

    @Test
    void createProduct() {
        ProductDTO result = productService.createProduct(productDTO, category_id);
        assertEquals(product.getTitle(), result.getTitle());
    }

    @Test
    void testCreateProductWithInvalidCategory() {
        productDTO = new ProductDTO();
        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(productDTO, 1L));
    }

    @Test
    void updateProduct() {
        ProductDTO result = productService.updateProduct(productDTO, product_id);
        assertEquals(product.getTitle(), result.getTitle());
    }

    @Test
    void testUpdateWithInvalidProductId() {
        productDTO = new ProductDTO();
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(productDTO, 1L));
    }

    @Test
    void deleteProduct() {
        assertDoesNotThrow(() -> productService.deleteProduct(product_id));
    }

    @Test
    void deleteProductWithInvalidProductId() {
        Long nonExistentProduct_id = 1L;
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(nonExistentProduct_id));
    }

    @Test
    void findProductById() {
        assertDoesNotThrow(() -> productService.findProductById(product_id));
    }

    @Test
    void findProductByIdWithInvalidProductId() {
        Long nonExistentProduct_id = 1L;
        assertThrows(ResourceNotFoundException.class, () -> productService.findProductById(nonExistentProduct_id));
    }

    @Test
    void findProductByCategory() {
        assertDoesNotThrow(() -> productService.findProductByCategory(category_id));
    }

    @Test
    void findProductByCategoryWithInvalidCategory() {
        Long nonExistentCategory_id = 1L;
        assertThrows(ResourceNotFoundException.class, () -> productService.findProductByCategory(nonExistentCategory_id));
    }

    @Test
    void searchProduct() {
        String keyword = "tai nghe";
        List<ProductDTO> result = productService.searchProduct(keyword);
        assertFalse(result.isEmpty());
    }

    @Test
    void updateImageProduct() {
        String newImageUrl = "new_image_url.jpg";
        productDTO = new ProductDTO();
        productDTO.setImageUrl(newImageUrl);
        ProductDTO updatedProduct = productService.updateImageProduct(productDTO, product_id);


        Product updatedProductInDatabase = productRepository.findById(product_id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "Product Id", product_id));


        assertEquals(newImageUrl, updatedProductInDatabase.getImageUrl());

        assertEquals(product_id, updatedProduct.getProductId());
        assertEquals(newImageUrl, updatedProduct.getImageUrl());
    }
}