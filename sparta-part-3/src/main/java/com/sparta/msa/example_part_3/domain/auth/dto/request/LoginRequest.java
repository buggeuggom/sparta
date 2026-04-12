package com.sparta.msa.example_part_3.domain.auth.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {

  String email;

  String password;

}
