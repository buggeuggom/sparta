package com.sparta.msa.lesson.domain.product.service;

import com.sparta.msa.lesson.domain.product.dto.request.ProductRequest;
import com.sparta.msa.lesson.domain.product.dto.response.ProductResponse;
import com.sparta.msa.lesson.domain.product.entity.Product;
import com.sparta.msa.lesson.domain.product.mapper.ProductMapper;
import com.sparta.msa.lesson.domain.product.repository.ProductRepository;
import com.sparta.msa.lesson.global.exception.DomainException;
import com.sparta.msa.lesson.global.exception.DomainExceptionCode;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sparta.msa.lesson.global.exception.DomainExceptionCode.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new DomainException(NOT_FOUND_PRODUCT));

        return productMapper.toResponse(product);
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = productMapper.toEntity(request);

        return productMapper.toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {

        Product product = productRepository.findById(id).orElseThrow(() -> new DomainException(NOT_FOUND_PRODUCT));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        return productMapper.toResponse(product);
    }

    @Transactional
    public void deleteById(Long id) {

        Product product = productRepository.findById(id).orElseThrow(() -> new DomainException(NOT_FOUND_PRODUCT));

        productRepository.delete(product);
    }

}
