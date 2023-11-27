package com.ecommerce_apis.application.serviceimpl;

import com.ecommerce_apis.domain.entities.Category;
import com.ecommerce_apis.domain.exceptions.ResourceNotFoundException;
import com.ecommerce_apis.domain.service.CategoryService;
import com.ecommerce_apis.infrastructure.gateways.CategoryMapper;
import com.ecommerce_apis.infrastructure.repositories.CategoryRepository;
import com.ecommerce_apis.presentation.dtos.CategoryDTO;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private static final Logger logger = Logger.getLogger(CategoryServiceImpl.class);

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    // create category by seller
    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {

        try {
            Category category = this.categoryMapper.convertToEntity(categoryDTO);
            category.setCreateAt(LocalDateTime.now());
            category.setDeleted(false);
            Category addCategory = this.categoryRepository.save(category);

            logger.info("Created category with id: " + addCategory.getId());
            return this.categoryMapper.convertToDTO(addCategory);
        } catch (Exception e) {
            logger.error("Failed to create category: " + e.getMessage());
            throw e;
        }
    }

    // update category
    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        try {
            Category category = this.categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));

            if (!category.isDeleted()) {
                category.setName(categoryDTO.getCategoryName());
                category.setDescription(categoryDTO.getDescription());
                category.setUpdateAt(LocalDateTime.now());

                Category updateCategory = this.categoryRepository.save(category);

                logger.info("Updated category with id: " + updateCategory.getId());
                return this.categoryMapper.convertToDTO(updateCategory);
            } else {
                throw new IllegalArgumentException("Cannot update category");
            }
        } catch (Exception e) {
            logger.error("Failed to update category: " + e.getMessage());
            throw e;
        }
    }


    //deleted category
    @Override
    public void deleteCategory(Long categoryId) {
        try {
            Category category = this.categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));

            if (category != null) {
                category.setDeleted(true);
                this.categoryRepository.save(category);
                logger.info("Deleted category with id: " + categoryId);
            } else {
                throw new IllegalArgumentException("Cannot delete category");
            }
        } catch (Exception e) {
            logger.error("Failed to delete category: " + e.getMessage());
            throw e;
        }
    }

    //get category by id
    @Override
    public CategoryDTO getCategoryId(Long categoryId) {
        try {
            Category category = this.categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));

            if (!category.isDeleted()) {
                logger.info("Retrieved category with id: " + categoryId);
                return this.categoryMapper.convertToDTO(category);
            } else {
                throw new IllegalArgumentException("Cannot get categories by id a deleted category");
            }
        } catch (Exception e) {
            logger.error("Failed to retrieve category by id: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<CategoryDTO> getCategories() {
        try {
            List<Category> categories = this.categoryRepository.findAll();
            List<CategoryDTO> categoryDTOs = categories.stream()
                    .map(this.categoryMapper::convertToDTO).collect(Collectors.toList());

            logger.info("Retrieved all categories");
            return categoryDTOs;
        } catch (Exception e) {
            logger.error("Failed to retrieve all categories: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public CategoryDTO updateImageCategory(CategoryDTO categoryDTO, Long categoryId) {
        try {
            Category category = this.categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));

            if (!category.isDeleted()) {
                category.setImage(categoryDTO.getCategoryImage());

                Category updateImageCategory = this.categoryRepository.save(category);

                logger.info("Updated image for category with id: " + updateImageCategory.getId());
                return this.categoryMapper.convertToDTO(updateImageCategory);
            } else {
                throw new IllegalArgumentException("Cannot update image category");
            }
        } catch (Exception e) {
            logger.error("Failed to update image category: " + e.getMessage());
            throw e;
        }
    }
}
