package com.sparta.msa.example_part_3.domain.auth.dto.request;

import com.sparta.msa.example_part_3.global.enums.Gender;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationRequest {

  String name;

  String phone;

  String email;

  String password;

  Gender gender;

}
