package com.sparta.msa.example_part_3.domain.product.service;

import com.sparta.msa.example_part_3.domain.category.entity.Category;
import com.sparta.msa.example_part_3.domain.category.repository.CategoryRepository;
import com.sparta.msa.example_part_3.domain.product.dto.request.ProductRequest;
import com.sparta.msa.example_part_3.domain.product.dto.response.ProductResponse;
import com.sparta.msa.example_part_3.domain.product.entity.Product;
import com.sparta.msa.example_part_3.domain.product.mapper.ProductMapper;
import com.sparta.msa.example_part_3.domain.product.repository.ProductRepository;
import com.sparta.msa.example_part_3.global.exception.DomainException;
import com.sparta.msa.example_part_3.global.exception.DomainExceptionCode;
import com.sparta.msa.example_part_3.global.response.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductMapper productMapper;
  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  @Transactional(readOnly = true)
  public PageResult<ProductResponse> findProductByPageable(Pageable pageable) {
    Page<Product> products = productRepository.findAllWithCategory(pageable);

    Page<ProductResponse> responses = productMapper.toResponsePage(products);

    return PageResult.<ProductResponse>builder()
        .page(responses)
        .build();
  }

  @Transactional
  public void create(ProductRequest request) {
    Category category = getCategory(request.getCategoryId());
    productRepository.save(productMapper.toEntity(request, category));
  }

  @Transactional
  public void update(Long productId, ProductRequest request) {
    Product product = getProduct(productId);
    Category category = getCategory(request.getCategoryId());

    product.setCategory(category);
    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setPrice(request.getPrice());
    product.setStock(request.getStock());
  }

  @Transactional
  public void delete(Long productId) {
    if (!productRepository.existsById(productId)) {
      throw new DomainException(DomainExceptionCode.NOT_FOUND_PRODUCT);
    }
    productRepository.deleteById(productId);
  }

  private Product getProduct(Long productId) {
    return productRepository.findById(productId)
        .orElseThrow(() -> new DomainException(DomainExceptionCode.NOT_FOUND_PRODUCT));
  }

  private Category getCategory(Long categoryId) {
    return categoryRepository.findById(categoryId)
        .orElseThrow(() -> new DomainException(DomainExceptionCode.NOT_FOUND_CATEGORY));
  }

}
