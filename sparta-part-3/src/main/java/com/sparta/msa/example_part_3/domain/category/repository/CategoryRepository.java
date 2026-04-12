package com.sparta.msa.example_part_3.domain.category.repository;

import com.sparta.msa.example_part_3.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

  Boolean existsByParentCategory_Id(Long parentCategoryId);

}
