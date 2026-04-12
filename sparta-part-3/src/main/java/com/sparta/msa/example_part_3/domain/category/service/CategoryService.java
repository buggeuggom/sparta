package com.sparta.msa.example_part_3.domain.category.service;

import com.sparta.msa.example_part_3.domain.category.dto.request.CategoryRequest;
import com.sparta.msa.example_part_3.domain.category.dto.response.CategoryResponse;
import com.sparta.msa.example_part_3.domain.category.entity.Category;
import com.sparta.msa.example_part_3.domain.category.repository.CategoryRepository;
import com.sparta.msa.example_part_3.global.exception.DomainException;
import com.sparta.msa.example_part_3.global.exception.DomainExceptionCode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;

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
  public void create(CategoryRequest request) {
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
