package com.sparta.msa.example_part_3.domain.category.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sparta.msa.example_part_3.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Table
@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  String name;

  @JsonBackReference
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  Category parentCategory;

  @Builder
  public Category(
      String name,
      Category parentCategory) {
    this.name = name;
    this.parentCategory = parentCategory;
  }

  public void setName(String name) {
    if (StringUtils.hasText(name)) {
      this.name = name;
    }
  }

  public void setParentCategory(Category parentCategory) {
    if (!ObjectUtils.isEmpty(parentCategory)) {
      this.parentCategory = parentCategory;
    }
  }
}
