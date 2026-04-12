package com.sparta.msa.lesson.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserCreateRequest {
    @NotBlank(message = "사용자 이름은 필수이며, 공백일 수 없습니다.")
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요.")
    private String username;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호는 010-XXXX-XXXX 형식이어야 합니다.")
    private String phoneNumber;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}
