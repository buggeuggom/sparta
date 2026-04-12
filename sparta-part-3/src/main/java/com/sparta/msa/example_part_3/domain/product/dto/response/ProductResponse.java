package com.sparta.msa.example_part_3.domain.product.dto.response;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {

  Long id;

  ProductCategoryResponse category;

  String name;

  String description;

  BigDecimal price;

  Integer stock;

  @Getter
  @Builder
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class ProductCategoryResponse {

    String id;

    String name;

  }
  
}
