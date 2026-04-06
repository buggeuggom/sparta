package com.sparta.msa.lesson.domain.product.mapper;

import com.sparta.msa.lesson.domain.product.dto.ProductDto;
import com.sparta.msa.lesson.domain.product.dto.request.ProductRequest;
import com.sparta.msa.lesson.domain.product.dto.response.ProductResponse;
import com.sparta.msa.lesson.domain.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {


    Product toEntity(ProductRequest request);

    @Mapping(target = "categoryId", source = "category.id")
    ProductResponse toResponse(Product product);
}