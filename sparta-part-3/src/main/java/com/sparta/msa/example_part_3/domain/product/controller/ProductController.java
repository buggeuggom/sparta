package com.sparta.msa.example_part_3.domain.product.controller;

import com.sparta.msa.example_part_3.domain.product.dto.request.ProductRequest;
import com.sparta.msa.example_part_3.domain.product.dto.response.ProductResponse;
import com.sparta.msa.example_part_3.domain.product.service.ProductService;
import com.sparta.msa.example_part_3.global.response.ApiResponse;
import com.sparta.msa.example_part_3.global.response.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/api/products")
public class ProductController {

  private final ProductService productService;

  @GetMapping
  public ApiResponse<PageResult<ProductResponse>> findProductByPageable(Pageable pageable) {
    return ApiResponse.ok(productService.findProductByPageable(pageable));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<Void> create(@Valid @RequestBody ProductRequest request) {
    productService.create(request);
    return ApiResponse.ok();
  }

  @PutMapping("/{productId}")
  public ApiResponse<Void> update(@PathVariable Long productId,
      @Valid @RequestBody ProductRequest request) {
    productService.update(productId, request);
    return ApiResponse.ok();
  }

  @DeleteMapping("/{productId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ApiResponse<Void> delete(@PathVariable Long productId) {
    productService.delete(productId);
    return ApiResponse.ok();
  }

}
