package com.sparta.msa.lesson.domain.product.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDto {

    Long categoryId;

    String name;

    String description;

    BigDecimal price;

    Integer stock;
}
