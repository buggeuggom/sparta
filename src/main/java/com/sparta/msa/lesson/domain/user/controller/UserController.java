package com.sparta.msa.lesson.domain.user.controller;

import com.sparta.msa.lesson.domain.user.dto.request.UserRequest;
import com.sparta.msa.lesson.domain.user.dto.response.UserResponse;
import com.sparta.msa.lesson.domain.user.service.UserService;
import com.sparta.msa.lesson.domain.user.service.UserServiceImpl;
import com.sparta.msa.lesson.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ApiResponse<UserResponse> create(@Valid @RequestBody UserRequest request) {
        // @Valid 통과 시: 이 코드가 실행됨
        return ApiResponse.ok(userService.save(request));
    }
}