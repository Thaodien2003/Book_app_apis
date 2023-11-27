package com.book_app_apis.application.serviceimpl;

import com.book_app_apis.domain.entities.Category;
import com.book_app_apis.domain.exceptions.ResourceNotFoundException;
import com.book_app_apis.domain.service.CategoryService;
import com.book_app_apis.infrastructure.gateways.CategoryMapper;
import com.book_app_apis.infrastructure.repositories.CategoryRepository;
import com.book_app_apis.presentation.dtos.CategoryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final MessageSource messageSource;
    private final String categoryMess;
    private final String categoryIdMess;
    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper, MessageSource messageSource) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.messageSource = messageSource;
        this.categoryMess = messageSource.getMessage("category.message", null, LocaleContextHolder.getLocale());
        this.categoryIdMess = messageSource.getMessage("category.message.id", null, LocaleContextHolder.getLocale());
    }

    // create category by seller
    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {

        try {
            Category category = this.categoryMapper.convertToEntity(categoryDTO);
            category.setCreateAt(LocalDateTime.now());
            category.setDeleted(false);
            Category addCategory = this.categoryRepository.save(category);

            String categoryLogInfo = messageSource.getMessage("category.create.log.info", null,
                    LocaleContextHolder.getLocale());
            logger.info(categoryLogInfo + "-" + addCategory.getId());
            return this.categoryMapper.convertToDTO(addCategory);
        } catch (Exception e) {
            String categoryLogError = messageSource.getMessage("category.crate.log.error", null,
                    LocaleContextHolder.getLocale());
            logger.error(categoryLogError + "-" + e.getMessage());
            throw e;
        }
    }

    // update category
    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        try {
            Category category = this.categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException(categoryMess, categoryIdMess, categoryId));

            if (!category.isDeleted()) {
                category.setName(categoryDTO.getCategoryName());
                category.setDescription(categoryDTO.getDescription());
                category.setUpdateAt(LocalDateTime.now());

                Category updateCategory = this.categoryRepository.save(category);

                String updateLogInfo = messageSource.getMessage("category.update.log.info", null,
                        LocaleContextHolder.getLocale());
                logger.info(updateLogInfo + "-" + updateCategory.getId());
                return this.categoryMapper.convertToDTO(updateCategory);
            } else {
                String errorRuntime = messageSource.getMessage("category.update.error.runtime", null,
                        LocaleContextHolder.getLocale());
                throw new IllegalArgumentException(errorRuntime);
            }
        } catch (Exception e) {
            String updateLogError = messageSource.getMessage("category.update.log.error", null,
                    LocaleContextHolder.getLocale());
            logger.error(updateLogError + "-" + e.getMessage());
            throw e;
        }
    }


    //deleted category
    @Override
    public void deleteCategory(Long categoryId) {
        try {
            Category category = this.categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException(categoryMess, categoryIdMess, categoryId));

            if (category != null) {
                category.setDeleted(true);
                this.categoryRepository.save(category);
                String deleteLogInfo = messageSource.getMessage("category.delete.log.info", null,
                        LocaleContextHolder.getLocale());
                logger.info(deleteLogInfo + "-" + categoryId);
            } else {
                throw new IllegalArgumentException("Cannot delete category");
            }
        } catch (Exception e) {
            String deleteLogError = messageSource.getMessage("category.delete.log.error", null,
                    LocaleContextHolder.getLocale());
            logger.error(deleteLogError + "-" + e.getMessage());
            throw e;
        }
    }

    //get category by id
    @Override
    public CategoryDTO getCategoryId(Long categoryId) {
        try {
            Category category = this.categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException(categoryMess, categoryIdMess, categoryId));

            if (!category.isDeleted()) {
                String getByIdLogInfo = messageSource.getMessage("category.getbyid.log.info", null,
                        LocaleContextHolder.getLocale());
                logger.info(getByIdLogInfo + "-" + categoryId);
                return this.categoryMapper.convertToDTO(category);
            } else {
                String getByIdErrorRuntime = messageSource.getMessage("category.getbyid.error.runtime", null,
                        LocaleContextHolder.getLocale());
                throw new IllegalArgumentException(getByIdErrorRuntime);
            }
        } catch (Exception e) {
            String getByIdLogError = messageSource.getMessage("category.getbyid.log.error", null,
                    LocaleContextHolder.getLocale());
            logger.error(getByIdLogError + "-" + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<CategoryDTO> getCategories() {
        try {
            List<Category> categories = this.categoryRepository.findAll();
            List<CategoryDTO> categoryDTOs = categories.stream()
                    .map(this.categoryMapper::convertToDTO).collect(Collectors.toList());
            String getAllLogInfo = messageSource.getMessage("category.getall.log.info", null,
                    LocaleContextHolder.getLocale());
            logger.info(getAllLogInfo);
            return categoryDTOs;
        } catch (Exception e) {
            String getAllLogError = messageSource.getMessage("category.getall.log.error", null,
                    LocaleContextHolder.getLocale());
            logger.error(getAllLogError + "-" + e.getMessage());
            throw e;
        }
    }

    @Override
    public CategoryDTO updateImageCategory(CategoryDTO categoryDTO, Long categoryId) {
        try {
            Category category = this.categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException(categoryMess, categoryIdMess, categoryId));

            if (!category.isDeleted()) {
                category.setImage(categoryDTO.getCategoryImage());

                Category updateImageCategory = this.categoryRepository.save(category);

                String imageLogInfo = messageSource.getMessage("category.image.log.info", null,
                        LocaleContextHolder.getLocale());
                logger.info(imageLogInfo + "-" + updateImageCategory.getId());
                return this.categoryMapper.convertToDTO(updateImageCategory);
            } else {
                String imageErrorRuntime = messageSource.getMessage("category.image.error.runtime", null,
                        LocaleContextHolder.getLocale());
                throw new IllegalArgumentException(imageErrorRuntime);
            }
        } catch (Exception e) {
            String imageLogError = messageSource.getMessage("category.image.log.error", null,
                    LocaleContextHolder.getLocale());
            logger.error(imageLogError + "-" + e.getMessage());
            throw e;
        }
    }
}
