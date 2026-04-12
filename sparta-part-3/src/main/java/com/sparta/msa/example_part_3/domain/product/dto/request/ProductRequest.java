package com.sparta.msa.example_part_3.domain.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {

  @NotNull
  Long categoryId;

  @NotBlank
  String name;

  String description;

  @NotNull
  BigDecimal price;

  @NotNull
  Integer stock;

}
