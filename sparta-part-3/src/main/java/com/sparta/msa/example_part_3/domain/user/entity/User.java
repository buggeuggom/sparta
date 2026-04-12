package com.sparta.msa.example_part_3.domain.user.entity;

import com.sparta.msa.example_part_3.global.entity.BaseEntity;
import com.sparta.msa.example_part_3.global.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Table
@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(nullable = false)
  String name;

  @Column(nullable = false)
  String phone;

  @Column(nullable = false, unique = true)
  String email;

  @Column(nullable = false)
  String password;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  Gender gender;

  @Builder
  public User(
      String name,
      String phone,
      String password,
      String email,
      Gender gender) {
    this.name = name;
    this.phone = phone;
    this.password = password;
    this.email = email;
    this.gender = gender;
  }
}
