package com.sparta.msa.example_part_3.domain.auth.service;

import com.sparta.msa.example_part_3.domain.auth.dto.request.LoginRequest;
import com.sparta.msa.example_part_3.domain.auth.dto.request.RegistrationRequest;
import com.sparta.msa.example_part_3.domain.auth.dto.response.LoginResponse;
import com.sparta.msa.example_part_3.domain.user.entity.User;
import com.sparta.msa.example_part_3.domain.user.repository.UserRepository;
import com.sparta.msa.example_part_3.global.exception.DomainException;
import com.sparta.msa.example_part_3.global.exception.DomainExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;

  @Transactional
  public void registration(RegistrationRequest request) {
    userRepository.save(User.builder()
        .name(request.getName())
        .phone(request.getPhone())
        .email(request.getEmail())
        .password(request.getPassword()) //TODO : 암호화 필요
        .gender(request.getGender())
        .build());
  }

  @Transactional
  public LoginResponse login(LoginRequest loginRequest) {
    User user = userRepository.findByEmail(loginRequest.getEmail())
        .orElseThrow(() -> new DomainException(DomainExceptionCode.NOT_FOUND_USER));

    //TODO : 패스워드 검증 로직 추가 필요

    return LoginResponse.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .build();
  }

  public LoginResponse getLoginInfo(Long userId, String email) {
    return LoginResponse.builder()
        .userId(userId)
        .email(email)
        .build();
  }

}
