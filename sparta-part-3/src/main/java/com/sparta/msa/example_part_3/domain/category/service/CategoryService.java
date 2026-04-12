package com.sparta.msa.example_part_3.domain.category.service;

import com.sparta.msa.example_part_3.domain.category.dto.request.CategoryRequest;
import com.sparta.msa.example_part_3.domain.category.dto.response.CategoryResponse;
import com.sparta.msa.example_part_3.domain.category.entity.Category;
import com.sparta.msa.example_part_3.domain.category.repository.CategoryRepository;
import com.sparta.msa.example_part_3.global.exception.DomainException;
import com.sparta.msa.example_part_3.global.exception.DomainExceptionCode;
import com.sparta.msa.example_part_3.global.util.JsonUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    //TODO: Constants 클래스로 통합 필요
    private static final String CACHE_KEY_CATEGORY = "cachedCategories";

    private final RedisTemplate<String, String> redisTemplate;

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAllForCacheAside() {
        // 1. 캐시에서 카테고리 구조 데이터 조회 시도
        String cachedCategories = redisTemplate.opsForValue().get(CACHE_KEY_CATEGORY);

        // 2. 캐시 히트
        if (!ObjectUtils.isEmpty(cachedCategories)) {
            log.info("Cache Hit: categoryStruct for key = {}", CACHE_KEY_CATEGORY);
            return JsonUtil.fromJsonList(cachedCategories, CategoryResponse.class);
        }

        // 3. 캐시 미스, 데이터베이스에서 조회 (findAll() 호출)
        log.info("Cache Miss: categoryStruct for key = {}", CACHE_KEY_CATEGORY);
        List<CategoryResponse> categories = findAll();

        // 4. 데이터베이스에서 조회한 데이터를 캐시에 저장
        if (!categories.isEmpty()) {
            String cacheCategories = JsonUtil.toJson(categories);
            redisTemplate.opsForValue().set(CACHE_KEY_CATEGORY, cacheCategories, 1, TimeUnit.HOURS);
        }

        return categories;
    }


    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        List<Category> categories = categoryRepository.findAll();

        Map<Long, CategoryResponse> categoryResponseMap = new HashMap<>();

        for (Category category : categories) {
            CategoryResponse response = CategoryResponse.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .childCategories(new ArrayList<>())
                    .build();
            categoryResponseMap.put(category.getId(), response);
        }

        List<CategoryResponse> rootCategories = new ArrayList<>();
        for (Category category : categories) {
            CategoryResponse categoryResponse = categoryResponseMap.get(category.getId());

            if (ObjectUtils.isEmpty(category.getParentCategory())) {
                rootCategories.add(categoryResponse);

            } else {
                CategoryResponse parentResponse
                        = categoryResponseMap.get(category.getParentCategory().getId());

                if (parentResponse != null) {
                    parentResponse.getChildCategories().add(categoryResponse);
                }
            }
        }

        return rootCategories;
    }

    @Transactional
    public void saveWriteThrough(CategoryRequest request) {
        try {
            create(request); // 데이터 베이스 신규 카테고리 저장
            updateCacheCategories(); // 캐시 업데이트 메서드 호출
        } catch (Exception e) {
            log.error("Failed to save category with Write-through: {}", e.getMessage(), e);
        }
    }

    private void updateCacheCategories() {
        try {
            List<CategoryResponse> categories = findAll();

            if (!categories.isEmpty()) {
                String cacheCategories = JsonUtil.toJson(categories);
                redisTemplate.opsForValue().set(CACHE_KEY_CATEGORY, cacheCategories);
            }
        } catch (Exception e) {
            log.error("Error updating cache key {}: {}", CACHE_KEY_CATEGORY, e.getMessage());
        }
    }

    @Transactional
    public void saveWriteBack(CategoryRequest request) {
        try {
            // 1. 캐시에서 카테고리 구조 데이터 조회 시도
            String cachedCategories = redisTemplate.opsForValue().get(CACHE_KEY_CATEGORY);

            List<CategoryResponse> categories = new ArrayList<>();

            // 2. 캐싱 데이터가 있다면 캐싱 데이터 불러오기
            if (!ObjectUtils.isEmpty(cachedCategories)) {
                categories = JsonUtil.fromJsonList(cachedCategories, CategoryResponse.class);
            }

            // 3. 캐시에 새로운 카테고리 데이터 추가
            CategoryResponse newCacheCategory = CategoryResponse.builder()
                    .name(request.getName())
                    .childCategories(new ArrayList<>())
                    .build();

            categories.add(newCacheCategory);

            // 4. Redis에 우선 저장
            String cacheCategories = JsonUtil.toJson(categories);
            redisTemplate.opsForValue().set(CACHE_KEY_CATEGORY, cacheCategories);

            // 5. 데이터베이스 저장 작업은 비동기로 처리
            saveToDatabaseAsync(request);

        } catch (Exception e) {
            log.error("Write-back 패턴 저장 실패: {}", e.getMessage());
        }
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveToDatabaseAsync(CategoryRequest request) {
        try {
            create(request);
        } catch (Exception e) {
            log.error("비동기 DB 저장 실패: {}", e.getMessage(), e);
        }
    }

    private void create(CategoryRequest request) {
        Category.CategoryBuilder category = Category.builder().name(request.getName());

        if (!ObjectUtils.isEmpty(request.getParentCategoryId())) {
            Category parentCategory = getCategory(request.getParentCategoryId());
            category.parentCategory(parentCategory);
        }

        categoryRepository.save(category.build());
    }

    @Transactional
    public void update(Long categoryId, CategoryRequest request) {
        Category category = getCategory(categoryId);

        if (!ObjectUtils.isEmpty(request.getParentCategoryId())) {
            Category parentCategory = getCategory(request.getParentCategoryId());
            category.setParentCategory(parentCategory);
        }

        category.setName(request.getName());
    }

    @Transactional
    public void deleteById(Long categoryId) {
        Category category = getCategory(categoryId);

        if (categoryRepository.existsByParentCategory_Id(categoryId)) {
            throw new DomainException(DomainExceptionCode.CATEGORY_HAS_CHILDREN);
        }
        categoryRepository.delete(category);
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.NOT_FOUND_CATEGORY));
    }

}