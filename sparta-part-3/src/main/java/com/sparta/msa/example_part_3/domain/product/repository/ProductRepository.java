package com.sparta.msa.example_part_3.domain.product.repository;

import com.sparta.msa.example_part_3.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  @Query("SELECT p FROM Product p JOIN FETCH p.category")
  Page<Product> findAllWithCategory(Pageable categoryId);

}
