package com.ecommerce_apis.infrastructure.repositories;

import com.ecommerce_apis.domain.entities.Category;
import com.ecommerce_apis.domain.entities.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private  CategoryRepository categoryRepository;

    Long productId;
    Long categoryId;
    Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setName("Laptop MSI");
        category.setDescription("Laptop gaming MSI 2022");
        categoryRepository.save(category);
        categoryId = category.getId();

        Product product = new Product();
        product.setTitle("Laptop gaming MSI");
        product.setDescription("Laptop gmaing MSI card RTX 4090, CPU core i9 1200th, ram 32GB");
        product.setColor("Balck, Red");
        product.setCategory(category);
        productRepository.save(product);
        productId = product.getId();
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteById(productId);
        categoryRepository.deleteById(categoryId);
    }

    @Test
    void findProductByCategory() {
        List<Product> products = productRepository.findProductByCategory(category);
        assertNotNull(products);
        assertEquals(1, products.size());
    }

    @Test
    void findByTitle() {
        String title = "Laptop gaming MSI";
        List<Product> products = productRepository.findByTitle(title);
        assertNotNull(products);
        assertEquals(1, products.size());
    }
}