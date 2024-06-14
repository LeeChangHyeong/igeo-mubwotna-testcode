package com.sparta.igeomubwotna.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
@Getter
public class SigninRequestDto {
    @NotBlank(message = "아이디는 공백일 수 없습니다.")
    private String userId;
    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    private String password;
}
