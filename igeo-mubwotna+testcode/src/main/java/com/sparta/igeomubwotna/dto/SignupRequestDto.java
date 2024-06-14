package com.sparta.igeomubwotna.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
public class SignupRequestDto {
    @Size(min = 10, max = 20, message = "사용자 ID는 최소 10글자 이상, 20글자 이하이어야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "사용자 ID는 알파벳 대소문자, 숫자로만 구성되어야 합니다.")
    private String userId;

    @Size(min = 10, message = "password는 최소 10글자 이상이어야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_])\\S{10,}$", message = "password는 알파벳 대소문자(a~z, A~Z), 숫자(0~9), 특수문자로만 구성되어야 합니다.")
    private String password;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String description;
}
