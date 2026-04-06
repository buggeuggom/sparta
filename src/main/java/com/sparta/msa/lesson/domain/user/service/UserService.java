package com.sparta.msa.lesson.domain.user.service;

import com.sparta.msa.lesson.domain.user.dto.request.UserRequest;
import com.sparta.msa.lesson.domain.user.dto.response.UserResponse;
import jakarta.validation.Valid;

public interface UserService {


    UserResponse save(UserRequest request);
}
