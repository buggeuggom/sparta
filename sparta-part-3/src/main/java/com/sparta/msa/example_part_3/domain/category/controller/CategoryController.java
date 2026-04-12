package com.sparta.msa.example_part_3.domain.category.controller;

import com.sparta.msa.example_part_3.domain.category.dto.request.CategoryRequest;
import com.sparta.msa.example_part_3.domain.category.dto.response.CategoryResponse;
import com.sparta.msa.example_part_3.domain.category.service.CategoryService;
import com.sparta.msa.example_part_3.global.response.ApiResponse;
import com.sparta.msa.example_part_3.global.util.JsonUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

  private final JsonUtil jsonUtil;
  private final CategoryService categoryService;

  @GetMapping
  public ApiResponse<List<CategoryResponse>> findAll(HttpSession httpSession) {

    return ApiResponse.ok(categoryService.findAll());
  }

  @GetMapping("/caches")
  public ApiResponse<List<CategoryResponse>> findAllByCaches(HttpSession httpSession) {
    final String CATEGORY_CACHE_KEY = "cachedCategoriesJson";

    String cachedCategoriesJson = (String) httpSession.getAttribute(CATEGORY_CACHE_KEY);

    if (cachedCategoriesJson == null) {
      List<CategoryResponse> freshCategories = categoryService.findAll();
      httpSession.setAttribute(CATEGORY_CACHE_KEY, JsonUtil.toJson(freshCategories));

      return ApiResponse.ok(freshCategories);
    } else {
      List<CategoryResponse> cachedCategories = JsonUtil.fromJsonList(
          cachedCategoriesJson, CategoryResponse.class
      );
      return ApiResponse.ok(cachedCategories);
    }
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<Void> create(@Valid @RequestBody CategoryRequest request) {
    categoryService.create(request);
    return ApiResponse.ok();
  }

  @PutMapping("/{categoryId}")
  public ApiResponse<Void> update(@PathVariable Long categoryId,
      @Valid @RequestBody CategoryRequest request) {
    categoryService.update(categoryId, request);
    return ApiResponse.ok();
  }

  @DeleteMapping("/{categoryId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ApiResponse<Void> delete(@PathVariable Long categoryId) {
    categoryService.deleteById(categoryId);
    return ApiResponse.ok();
  }


}
