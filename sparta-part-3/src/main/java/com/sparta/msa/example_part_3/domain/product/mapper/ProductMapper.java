package com.sparta.msa.example_part_3.domain.product.mapper;

import com.sparta.msa.example_part_3.domain.category.entity.Category;
import com.sparta.msa.example_part_3.domain.product.dto.request.ProductRequest;
import com.sparta.msa.example_part_3.domain.product.dto.response.ProductResponse;
import com.sparta.msa.example_part_3.domain.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  @Mapping(target = "category.id", source = "category.id")
  @Mapping(target = "category.name", source = "category.name")
  ProductResponse toResponse(Product product);

  @Mapping(target = "category", source = "category")
  @Mapping(target = "name", source = "request.name")
  Product toEntity(ProductRequest request, Category category);

  default Page<ProductResponse> toResponsePage(Page<Product> products) {
    return products.map(this::toResponse);
  }

}
