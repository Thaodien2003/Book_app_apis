package com.book_app_apis.application.serviceimpl;

import com.book_app_apis.application.payloads.response.ProductResponse;
import com.book_app_apis.domain.entities.Category;
import com.book_app_apis.domain.entities.Product;
import com.book_app_apis.domain.entities.ProductListener;
import com.book_app_apis.domain.entities.Rating;
import com.book_app_apis.domain.exceptions.ResourceNotFoundException;
import com.book_app_apis.domain.service.ProductRedisService;
import com.book_app_apis.domain.service.ProductService;
import com.book_app_apis.infrastructure.gateways.ProductMapper;
import com.book_app_apis.infrastructure.repositories.CategoryRepository;
import com.book_app_apis.infrastructure.repositories.ProductRepository;
import com.book_app_apis.presentation.dtos.ProductDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final MessageSource messageSource;
    private final String productMess;
    private final String productMessId;
    private final String categoryMess;
    private final String categoryMessId;
    private final ProductRedisService productRedisService;
    private static final String cacheKey = "all_products:%d:%d:%s:%s";
    private static final Logger logger = LoggerFactory.getLogger(ProductListener.class);

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper,
                              CategoryRepository categoryRepository,
                              MessageSource messageSource, ProductRedisService productRedisService) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
        this.messageSource = messageSource;
        this.productRedisService = productRedisService;
        this.productMess = messageSource.getMessage("product.message", null,
                LocaleContextHolder.getLocale());
        this.productMessId =  messageSource.getMessage("product.message.id", null,
                LocaleContextHolder.getLocale());
        this.categoryMess = messageSource.getMessage("category.message", null,
                LocaleContextHolder.getLocale());
        this.categoryMessId = messageSource.getMessage("category.message.id", null,
                LocaleContextHolder.getLocale());
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO, Long categoryId) {
        Category category = this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(categoryMess, categoryMessId, categoryId));

        if (!category.isDeleted()) {
            Product product = this.productMapper.convertToEntity(productDTO);
            product.setTitle(productDTO.getTitle());
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            product.setDiscountedPrice(productDTO.getDiscountedPrice());
            product.setQuantity(productDTO.getQuantity());
            product.setBrand(productDTO.getBrand());
            product.setColor(productDTO.getColor());
            product.setSizes(productDTO.getSize());
            product.setCategory(category);
            product.setDeleted(false);
            product.setCreatedAt(LocalDateTime.now());

            Product addProduct = productRepository.save(product);

            String createProductLogInfo = messageSource.getMessage("product.create.log.info", null,
                    LocaleContextHolder.getLocale());
            logger.info(createProductLogInfo + "-" + addProduct);
            return productMapper.convertToDTO(addProduct);
        } else {
            String createProductLogError = messageSource.getMessage("product.create.log.error", null,
                    LocaleContextHolder.getLocale());
            String createProductError = messageSource.getMessage("product.create.error.runtime", null,
                    LocaleContextHolder.getLocale());
            logger.error(createProductLogError);
            throw new IllegalArgumentException(createProductError);
        }
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(productMess, productMessId, productId));

        if (!product.isDeleted()) {
            product.setTitle(productDTO.getTitle());
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            product.setDiscountedPrice(productDTO.getDiscountedPrice());
            product.setQuantity(productDTO.getQuantity());
            product.setBrand(productDTO.getBrand());
            product.setColor(productDTO.getColor());
            product.setSizes(productDTO.getSize());
            product.setUpdateAt(LocalDateTime.now());

            Product updateProduct = this.productRepository.save(product);

            String updateLogInfo = messageSource.getMessage("product.update.log.info", null,
                    LocaleContextHolder.getLocale());
            logger.info(updateLogInfo + "-" + updateProduct);
            return this.productMapper.convertToDTO(updateProduct);
        } else {
            String updateLogInfo = messageSource.getMessage("product.update.log.error", null,
                    LocaleContextHolder.getLocale());
            String updateError = messageSource.getMessage("product.update.error.runtime", null,
                    LocaleContextHolder.getLocale());
            logger.error(updateLogInfo);
            throw new IllegalArgumentException(updateError);
        }
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(productMess, productMessId, productId));

        try {
            String deletedLogInfo = messageSource.getMessage("product.delete.log.info", null,
                    LocaleContextHolder.getLocale());
            product.setDeleted(true);
            logger.info(deletedLogInfo);
            this.productRepository.save(product);
        } catch (Exception e) {
            String deletedLogError = messageSource.getMessage("product.delete.log.error", null,
                    LocaleContextHolder.getLocale());
            String deletedError = messageSource.getMessage("product.delete.error.runtime", null,
                    LocaleContextHolder.getLocale());
            logger.error(deletedLogError);
            throw new IllegalArgumentException(deletedError);
        }
    }

    @Override
    public ProductDTO findProductById(Long productId) {
        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(productMess, productMessId, productId));

        if (!product.isDeleted()) {
            String getByIdLogInfo = messageSource.getMessage("product.getbyid.log.info", null,
                    LocaleContextHolder.getLocale());
            logger.info(getByIdLogInfo + "-" + productId);
            return this.productMapper.convertToDTO(product);
        } else {
            String getByIdLogError = messageSource.getMessage("product.getbyid.log.error", null,
                    LocaleContextHolder.getLocale());
            String getByIdError = messageSource.getMessage("product.getbyid.error.runtime", null,
                    LocaleContextHolder.getLocale());
            logger.error(getByIdLogError);
            throw new IllegalArgumentException(getByIdError);
        }
    }

    @Override
    public List<ProductDTO> findProductByCategory(Long categoryId) throws JsonProcessingException {
        // check data in cache
        List<ProductDTO> productDTOs = productRedisService.getAllProducts(cacheKey);

        if (productDTOs == null) {
            Category category = this.categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException(categoryMess, categoryMessId, categoryId));

            if (!category.isDeleted()) {
                List<Product> products = this.productRepository.findProductByCategory(category);
                String productLogInfo = messageSource.getMessage("product.category.log.info", null,
                        LocaleContextHolder.getLocale());
                logger.info(productLogInfo + "-" + categoryId + " with product" + products);

                // fill product is not delete
                productDTOs = products.stream()
                        .filter(product -> !product.isDeleted())
                        .map(this.productMapper::convertToDTO)
                        .collect(Collectors.toList());

                // save cache
                productRedisService.saveAllProducts(productDTOs, cacheKey);
            } else {
                String productLogError = messageSource.getMessage("product.category.log.error", null,
                        LocaleContextHolder.getLocale());
                String productError = messageSource.getMessage("product.category.error.runtime", null,
                        LocaleContextHolder.getLocale());
                logger.error(productLogError);
                throw new IllegalArgumentException(productError);
            }
        }

        return productDTOs;
    }


    @Override
    public ProductResponse getAllProduct(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) throws JsonProcessingException {
        String allProductLogInfo = messageSource.getMessage("product.getall.log.info", null,
                LocaleContextHolder.getLocale());
        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable page = PageRequest.of(pageNumber, pageSize, sort);

        // check data in cache
        List<ProductDTO> productDTOs = productRedisService.getAllProducts(cacheKey);

        Page<Product> pageProduct = null;
        if (productDTOs == null) {
            pageProduct = this.productRepository.findAll(page);

            List<Product> allProducts = pageProduct.getContent();
            productDTOs = allProducts.stream()
                    .filter(product -> !product.isDeleted())
                    .map(this.productMapper::convertToDTO)
                    .collect(Collectors.toList());

            productRedisService.saveAllProducts(productDTOs, cacheKey);
        }

        ProductResponse productResponse = new ProductResponse();

        productResponse.setProducts(productDTOs);
        assert pageProduct != null;
        productResponse.setProducts(productDTOs);
        productResponse.setPageNumber(pageProduct.getNumber());
        productResponse.setPageSize(pageProduct.getSize());
        productResponse.setTotalElements(pageProduct.getTotalElements());
        productResponse.setTotalPages(pageProduct.getTotalPages());
        productResponse.setLastPage(pageProduct.isLast());
        logger.info(allProductLogInfo);
        return productResponse;
    }

    @Override
    public List<ProductDTO> findNewProducts() throws JsonProcessingException {
        //create key
        String cacheKeyNewProduct = "new_products";

        List<ProductDTO> newProductDTOs = productRedisService.getAllProducts(cacheKeyNewProduct);

        if (newProductDTOs == null) {
            List<Product> products = this.productRepository.findAll();

            List<Product> validProducts = new ArrayList<>(products.stream()
                    .filter(product -> !product.isDeleted())
                    .toList());

            validProducts.sort(Comparator.comparing(Product::getCreatedAt).reversed());

            int numberOfNewProducts = 10;
            List<Product> newProducts = validProducts.stream()
                    .limit(numberOfNewProducts)
                    .toList();

            String allProductDBLogInfo = messageSource.getMessage("product.getalldb.log.info", null,
                    LocaleContextHolder.getLocale());
            logger.info(allProductDBLogInfo);

            newProductDTOs = newProducts.stream().map(this.productMapper::convertToDTO)
                    .collect(Collectors.toList());

            productRedisService.saveAllProducts(newProductDTOs, cacheKeyNewProduct);
        }

        return newProductDTOs;
    }

    @Override
    public List<ProductDTO> searchProduct(String keyword) throws JsonProcessingException {
        List<ProductDTO> searchResultDTOs = productRedisService.getAllProducts(cacheKey);

        if (searchResultDTOs == null) {
            List<Product> products = this.productRepository.findByTitle("%" + keyword + "%");
            String searchProductLogInfo = messageSource.getMessage("product.search.log.info", null,
                    LocaleContextHolder.getLocale());
            logger.info(searchProductLogInfo + "-" + keyword);

            searchResultDTOs = products.stream().map(this.productMapper::convertToDTO)
                    .collect(Collectors.toList());

            productRedisService.saveAllProducts(searchResultDTOs, cacheKey);
        }

        return searchResultDTOs;
    }


    @Override
    public ProductDTO updateImageProduct(ProductDTO productDTO, Long productId) {
        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(productMess, productMessId, productId));

        String LogInfo = messageSource.getMessage("prouduct.upload.log.info", null,
                LocaleContextHolder.getLocale());

        if (!product.isDeleted()) {
            product.setImageUrl(productDTO.getImageUrl());
            Product updateImageProduct = this.productRepository.save(product);
            logger.info(LogInfo);
            return this.productMapper.convertToDTO(updateImageProduct);
        } else {
            String LogError = messageSource.getMessage("product.upload.log.error", null,
                    LocaleContextHolder.getLocale());
            String uploadError = messageSource.getMessage("product.upload.error.runtime", null,
                    LocaleContextHolder.getLocale());
            logger.error(LogError);
            throw new IllegalArgumentException(uploadError);
        }
    }

    @Override
    public Page<ProductDTO> getFilterProduct(String category, List<String> colors, List<String> sizes,
                                             Integer minPrice, Integer maxPrice, Integer minDiscount, String sortDir,
                                             String stock, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        List<Product> products = this.productRepository.
                filterProducts(category, minPrice, maxPrice, minDiscount, sortDir);

        if (!colors.isEmpty()) {
            products = products.stream().filter(product -> colors.stream().anyMatch(
                    c -> c.equalsIgnoreCase(product.getColor())
            )).collect(Collectors.toList());
        }

        if (stock != null) {
            if (stock.equals("in-stock")) {
                products = products.stream().filter(product -> product.getQuantity() > 0)
                        .collect(Collectors.toList());
            } else if (stock.equals("out-of-stock")) {
                products = products.stream().filter(product -> product.getQuantity() < 1)
                        .collect(Collectors.toList());
            }
        }

        int totalProducts = products.size();
        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), products.size());

        List<Product> pageContent = products.subList(startIndex, endIndex);

        List<ProductDTO> productDTOs = pageContent.stream().map(this.productMapper::convertToDTO)
                .collect(Collectors.toList());
        logger.info("Filter product with category - " + category + "colors" + colors +
                "size" + sizes + "minPrice" + minPrice + "minDiscounte" + minDiscount);
        return new PageImpl<>(productDTOs, pageable, totalProducts);
    }

    public void updateAverageRating(long productId) {
        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(productMess, productMessId, productId));

        if (product != null && !product.getRatings().isEmpty()) {
            double totalRating = product.getRatings().stream()
                    .mapToDouble(Rating::getRating)
                    .sum();

            double averageRating = totalRating / product.getRatings().size();
            product.setNumRatings(averageRating);
            product.setUpdateAt(LocalDateTime.now());
            productRepository.save(product);
        }
    }

    @Scheduled(fixedRate = 300000)
    public void updateAverageRatingsForAllProducts() {
        Iterable<Product> products = productRepository.findAll();
        for (Product product : products) {
            updateAverageRating(product.getId());
        }
    }

}
