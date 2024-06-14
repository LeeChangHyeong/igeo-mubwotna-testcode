package com.sparta.igeomubwotna.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserUpdateRequestDto {
    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private String currentPassword;  // 현재 저장되어 있는 비밀번호와 일치할 시에만 새로운 비밀번호로 변경 가능

    @Size(min = 10, message = "password는 최소 10글자 이상이어야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_])\\S{10,}$", message = "password는 알파벳 대소문자(a~z, A~Z), 숫자(0~9), 특수문자로만 구성되어야 합니다.")
    private String newPassword;
}