package com.book_app_apis.application.serviceimpl;

import com.book_app_apis.domain.service.ProductRedisService;
import com.book_app_apis.presentation.dtos.ProductDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ProductRedisServiceImpl implements ProductRedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper redisObjectMapper;

    public ProductRedisServiceImpl(RedisTemplate<String, Object> redisTemplate, ObjectMapper redisObjectMapper) {
        this.redisTemplate = redisTemplate;
        this.redisObjectMapper = redisObjectMapper;
    }

    @Override
    public void clear() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().flushAll();
    }

    @Override
    public List<ProductDTO> getAllProducts(String cacheKey) throws JsonProcessingException {
        String json = (String) redisTemplate.opsForValue().get(cacheKey);
        return json != null ? redisObjectMapper.readValue(json, new TypeReference<>() {
        }) : null;
    }


    @Override
    public void saveAllProducts(List<ProductDTO> productDTOS, String cacheKey) throws JsonProcessingException {
        String json = redisObjectMapper.writeValueAsString(productDTOS);
        redisTemplate.opsForValue().set(cacheKey, json);
    }
}
