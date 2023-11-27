package com.ecommerce_apis.application.serviceimpl;

import com.ecommerce_apis.application.payloads.response.ProductResponse;
import com.ecommerce_apis.domain.entities.Category;
import com.ecommerce_apis.domain.entities.Product;
import com.ecommerce_apis.domain.entities.User;
import com.ecommerce_apis.domain.exceptions.ResourceNotFoundException;
import com.ecommerce_apis.domain.exceptions.UserException;
import com.ecommerce_apis.domain.service.ProductService;
import com.ecommerce_apis.infrastructure.gateways.ProductMapper;
import com.ecommerce_apis.infrastructure.repositories.CategoryRepository;
import com.ecommerce_apis.infrastructure.repositories.ProductRepository;
import com.ecommerce_apis.infrastructure.repositories.UserRepository;
import com.ecommerce_apis.presentation.dtos.ProductDTO;
import org.apache.log4j.Logger;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private static final String PRODUCT_CACHE_KEY = "product_cache_create";
    private final RedisTemplate<String, Object> redisTemplate;
    private static final Logger logger = Logger.getLogger(ProductServiceImpl.class);

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper,
                              CategoryRepository categoryRepository,
                              UserRepository userRepository,
                              RedisTemplate<String, Object> redisTemplate) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @CachePut(value = PRODUCT_CACHE_KEY)
    @Override
    public ProductDTO createProduct(ProductDTO productDTO, Long categoryId, String sellerId) throws UserException {
        Category category = this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Category id", categoryId));

        User seller = userRepository.findById(sellerId).orElse(null);

        if (seller == null) {
            throw new UserException("Seller not found");
        }

        if (!category.isDeleted()) {
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
            product.setSeller(seller);
            product.setDeleted(false);
            product.setCreatedAt(LocalDateTime.now());

            // save product in database
            Product addProduct = productRepository.save(product);

            // save product in cache
            redisTemplate.opsForList().rightPush(PRODUCT_CACHE_KEY, productMapper.convertToDTO(addProduct));

            logger.info("Product create successfully - " + addProduct);
            return productMapper.convertToDTO(addProduct);
        } else {
            logger.error("Product create Failed");
            throw new IllegalArgumentException("Cannot create product");
        }
    }

    @Caching(put = {
            @CachePut(value = "ProductDTO_Update", key = "#productId")}
    )
    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId, String sellerId) throws UserException {
        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "Product Id", productId));

        User seller = userRepository.findById(sellerId).orElse(null);

        if (seller == null) {
            throw new UserException("Seller not found");
        }

        if (!product.isDeleted() && Objects.equals(product.getSeller().getUser_id(), sellerId)) {
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

            // check product in cache
            ProductDTO cachedProduct = getCachedProduct(productId);

            // if product in not cache, save product
            if (cachedProduct == null) {
                logger.info("Save product in cached - " + updateProduct);
                // save product in cache
                redisTemplate.opsForValue().set("ProductDTO::" + product.getId(), productMapper.convertToDTO(product));
            }

            logger.info("Update product successfully - " + updateProduct);
            return this.productMapper.convertToDTO(updateProduct);
        } else {
            logger.error("Update product failed");
            throw new IllegalArgumentException("Cannot update product");
        }
    }


    @Override
    public void deleteProduct(Long productId, String sellerId) throws UserException {
        User seller = userRepository.findById(sellerId).orElse(null);

        if (seller == null) {
            throw new UserException("Seller not found");
        }

        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "Product Id", productId));

        if (Objects.equals(product.getSeller().getUser_id(), sellerId)) {
            product.setDeleted(true);
            logger.info("Delete product successfully");
            this.productRepository.save(product);
        } else {
            logger.error("Delete product failed");
            throw new IllegalArgumentException("Cannot delete product");
        }
    }

    @Override
    public ProductDTO findProductById(Long productId) {
        // Nếu không có trong cache, đọc từ cơ sở dữ liệu
        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "Product Id", productId));

        if (!product.isDeleted()) {
            logger.info("Get product by id - " + productId);
            return this.productMapper.convertToDTO(product);
        } else {
            logger.error("Get product by id failed");
            throw new IllegalArgumentException("Cannot find Product By id a deleted product");
        }
    }

    @Cacheable(value = "productsByCategory", key = "#categoryId")
    @Override
    public List<ProductDTO> findProductByCategory(Long categoryId) {
        Category category = this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "category id", categoryId));

        if (!category.isDeleted()) {
            List<Product> products = this.productRepository.findProductByCategory(category);
            logger.info("Get product by category - " + categoryId + "with product" + products);
            return products.stream()
                    .filter(product -> !product.isDeleted()) // Lọc ra sản phẩm không bị xóa
                    .map(this.productMapper::convertToDTO)
                    .collect(Collectors.toList());
        } else {
            logger.error("Get product by category failed");
            throw new IllegalArgumentException("Cannot find products for a deleted category");
        }
    }

    @Cacheable(value = "products", key = "{#pageNumber, #pageSize, #sortBy, #sortDir}")
    @Override
    public ProductResponse getAllProduct(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable page = PageRequest.of(pageNumber, pageSize, sort);

        Page<Product> pageProduct = this.productRepository.findAll(page);

        List<Product> allProducts = pageProduct.getContent();
        // Lọc ra các sản phẩm có trường deleted là false
        List<ProductDTO> productDTOs = allProducts.stream()
                .filter(product -> !product.isDeleted())
                .map(this.productMapper::convertToDTO)
                .collect(Collectors.toList());

        ProductResponse productResponse = new ProductResponse();

        productResponse.setProducts(productDTOs);
        productResponse.setPageNumber(pageProduct.getNumber());
        productResponse.setPageSize(pageProduct.getSize());
        productResponse.setTotalElements(pageProduct.getTotalElements());
        productResponse.setTotalPages(pageProduct.getTotalPages());
        productResponse.setLastPage(pageProduct.isLast());
        logger.info("Get all products");
        return productResponse;
    }

    @Override
    public List<ProductDTO> findAllProducts() {
        List<Product> products = this.productRepository.findAll();
        logger.info("Get all product in db");
        return products.stream().map(this.productMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "searchedProducts", key = "#keyword")
    @Override
    public List<ProductDTO> searchProduct(String keyword) {
        List<Product> products = this.productRepository.findByTitle("%" + keyword + "%");
        logger.info("Search product with keyword - " + keyword);
        return products.stream().map(this.productMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO updateImageProduct(ProductDTO productDTO, Long productId, String sellerId) throws UserException {
        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "Product Id", productId));

        User seller = userRepository.findById(sellerId).orElse(null);

        if (seller == null) {
            throw new UserException("Seller not found");
        }

        if (!product.isDeleted() && Objects.equals(product.getSeller().getUser_id(), sellerId)) {
            product.setImageUrl(productDTO.getImageUrl());
            Product updateImageProduct = this.productRepository.save(product);
            logger.info("Upload image product successfully ");
            return this.productMapper.convertToDTO(updateImageProduct);
        } else {
            logger.error("Upload image product failed");
            throw new IllegalArgumentException("Cannot update image a product");
        }
    }

    @Cacheable(value = "filteredProducts", key = "{#category, #colors, #sizes, #minPrice, #maxPrice, #minDiscount, #sortDir," +
            " #stock, #pageNumber, #pageSize}")
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

//        Page<Product> filterdProduct = new PageImpl<>(pageContent, pageable, products.size());
        logger.info("Filter product with category - " + category + "colors" + colors +
                "size" + sizes + "minPrice" + minPrice + "minDiscounte" + minDiscount);
        return new PageImpl<>(productDTOs, pageable, totalProducts);
    }

    @Override
    public List<ProductDTO> findAllProductsBySeller(String sellerId) throws UserException {
        User seller = userRepository.findById(sellerId).orElse(null);

        if (seller == null) {
            throw new UserException("Seller not found");
        }

        if (!seller.isDeleted()) {
            List<Product> products = this.productRepository.findProductBySeller(seller);
            logger.info("Get product by seller with id - " + sellerId);
            return products.stream()
                    .filter(product -> !product.isDeleted()) // Lọc ra sản phẩm không bị xóa
                    .map(this.productMapper::convertToDTO)
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("Cannot find products for a deleted or seller deleted");
        }
    }

    //get cachedProduct by id
    private ProductDTO getCachedProduct(Long productId) {
        try {
            // Thử lấy sản phẩm từ cache
            return (ProductDTO) redisTemplate.opsForValue().get("ProductDTO::" + productId);
        } catch (Exception e) {
            // Xử lý lỗi (nếu có), có thể log lỗi nếu cần
            return null;
        }
    }
}
