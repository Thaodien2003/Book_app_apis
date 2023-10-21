package com.ecommerce_apis.domain.service.impl;

import com.ecommerce_apis.presentation.dtos.CategoryDTO;
import com.ecommerce_apis.domain.service.CategoryService;
import com.ecommerce_apis.domain.entities.Category;
import com.ecommerce_apis.domain.exceptions.ResourceNotFoundException;
import com.ecommerce_apis.domain.repositories.CategoryRepository;
import com.ecommerce_apis.infrastructure.gateways.CategoryMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = this.categoryMapper.convertToEntity(categoryDTO);
        category.setCreateAt(LocalDateTime.now());
        Category addCategory = this.categoryRepository.save(category);
        return this.categoryMapper.convertToDTO(addCategory);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category category = this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));

        category.setName(categoryDTO.getCategoryName());
        category.setDescription(categoryDTO.getDescription());
        category.setUpdateAt(LocalDateTime.now());

        Category updateCategory = this.categoryRepository.save(category);

        return this.categoryMapper.convertToDTO(updateCategory);
    }

    @Override
    public void deleteCategory(Long categoryId) {

        Category category = this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));

        this.categoryRepository.delete(category);
    }

    @Override
    public CategoryDTO getCategoryId(Long categoryId) {
        Category category = this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));
        return this.categoryMapper.convertToDTO(category);
    }

    @Override
    public List<CategoryDTO> getCategories() {
        List<Category> categories = this.categoryRepository.findAll();
        return categories.stream()
                .map(this.categoryMapper::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public CategoryDTO updateImageCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category category = this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));

        category.setImage(categoryDTO.getCategoryImage());

        Category updateImageCategory = this.categoryRepository.save(category);

        return this.categoryMapper.convertToDTO(updateImageCategory);
    }
}
