package com.sparta.msa.example_part_3.domain.auth.controller;

import com.sparta.msa.example_part_3.domain.auth.dto.request.LoginRequest;
import com.sparta.msa.example_part_3.domain.auth.dto.request.RegistrationRequest;
import com.sparta.msa.example_part_3.domain.auth.dto.response.LoginResponse;
import com.sparta.msa.example_part_3.domain.auth.service.AuthService;
import com.sparta.msa.example_part_3.global.exception.DomainException;
import com.sparta.msa.example_part_3.global.exception.DomainExceptionCode;
import com.sparta.msa.example_part_3.global.response.ApiResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  @PostMapping("registration")
  public ApiResponse<Void> registration(@Valid @RequestBody RegistrationRequest request) {
    authService.registration(request);
    return ApiResponse.ok();
  }
  
  @PostMapping("/login")
  public ApiResponse<LoginResponse> login(HttpSession httpSession,
      @Valid @RequestBody LoginRequest loginRequest) {
    LoginResponse loginResponse = authService.login(loginRequest);

    httpSession.setAttribute("userId", loginResponse.getUserId());
    httpSession.setAttribute("email", loginResponse.getEmail());

    log.info("session id : {}", httpSession.getId());

    return ApiResponse.ok(loginResponse);
  }

  @GetMapping("/status")
  public ApiResponse<LoginResponse> checkStatus(HttpSession httpSession) {
    Long userId = (Long) httpSession.getAttribute("userId");
    String email = (String) httpSession.getAttribute("email");

    if (ObjectUtils.isEmpty(userId) && ObjectUtils.isEmpty(email)) {
      throw new DomainException(DomainExceptionCode.NOT_FOUND_USER);
    }

    return ApiResponse.ok(authService.getLoginInfo(userId, email));
  }

  @GetMapping("/logout")
  public ApiResponse<Void> logout(HttpSession httpSession) {
    httpSession.invalidate();
    return ApiResponse.ok();
  }

}
