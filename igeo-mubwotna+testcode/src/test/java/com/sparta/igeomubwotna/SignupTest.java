package com.sparta.igeomubwotna;

import com.sparta.igeomubwotna.dto.SignupRequestDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SignupTest {

    private Validator validator; // 유효성 검사를 수행할 Validator 객체
    SignupRequestDto signupRequestDto;
    @BeforeEach
    void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory(); // ValidatorFactory 인스턴스 생성
        validator = factory.getValidator(); // Validator 인스턴스 생성
    }

    @BeforeEach
    void setUp() {
        signupRequestDto = new SignupRequestDto();

        signupRequestDto.setUserId("lchNumber9");
        signupRequestDto.setPassword("Dlckdgud11!");
        signupRequestDto.setName("이창형");
        signupRequestDto.setEmail("shlee509@nate.com");
        signupRequestDto.setDescription("hi");
    }

    @Nested
    @DisplayName("회원가입 아이디 오류 테스트")
    class UserIdTest {
        @Test
        @DisplayName("10자 미만일 때")
        void test1() {
            signupRequestDto.setUserId("short");
            Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(signupRequestDto); // 유효성 검사 수행
            assertEquals(1, violations.size()); // 유효성 검사 실패 개수 확인
            assertTrue(violations.iterator().next().getMessage().contains("사용자 ID는 최소 10글자 이상, 20글자 이하이어야 합니다.")); // 오류 메시지 검증
        }

        @Test
        @DisplayName("20자 초과일 때")
        void test2() {
            signupRequestDto.setUserId("longlonglonglonglonglonglonglong");
            Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(signupRequestDto); // 유효성 검사 수행
            assertEquals(1, violations.size()); // 유효성 검사 실패 개수 확인
            assertTrue(violations.iterator().next().getMessage().contains("사용자 ID는 최소 10글자 이상, 20글자 이하이어야 합니다.")); // 오류 메시지 검증
        }

        @Test
        @DisplayName("한글이 있을 때")
        void test3() {
            signupRequestDto.setUserId("userid한글이있어요");  // 한글이 포함된 userId 설정
            Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(signupRequestDto);  // 유효성 검사 수행
            System.out.println(violations.iterator().next().getMessage());
            assertTrue(violations.iterator().next().getMessage().contains("사용자 ID는 알파벳 대소문자, 숫자로만 구성되어야 합니다."));  // 오류 메시지 검증
        }
    }

    @Nested
    @DisplayName("회원가입 비밀번호 오류 테스트")
    class PasswordTest {
        @Test
        @DisplayName("10자 미만일 때")
        void test1() {
            signupRequestDto.setPassword("Dlckd");
            Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(signupRequestDto); // 유효성 검사 수행
            System.out.println(violations.iterator().next().getMessage());
            assertTrue(violations.iterator().next().getMessage().contains("password는 최소 10글자 이상이어야 합니다.")); // 오류 메시지 검증
        }

        @Test
        @DisplayName("알파벳 대문자가 없을 때")
        void test2() {
            signupRequestDto.setPassword("dlckdgud11!");
            Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(signupRequestDto); // 유효성 검사 수행
            assertTrue(violations.iterator().next().getMessage().contains("password는 알파벳 대소문자(a~z, A~Z), 숫자(0~9), 특수문자로만 구성되어야 합니다.")); // 오류 메시지 검증
        }

        @Test
        @DisplayName("알파벳 소문자가 없을 때")
        void test3() {
            signupRequestDto.setPassword("DLCKDGUD11!");
            Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(signupRequestDto); // 유효성 검사 수행
            assertTrue(violations.iterator().next().getMessage().contains("password는 알파벳 대소문자(a~z, A~Z), 숫자(0~9), 특수문자로만 구성되어야 합니다.")); // 오류 메시지 검증
        }

        @Test
        @DisplayName("숫자가 없을 때")
        void test4() {
            signupRequestDto.setPassword("Dlckdgud!!!!@#");
            Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(signupRequestDto); // 유효성 검사 수행
            assertTrue(violations.iterator().next().getMessage().contains("password는 알파벳 대소문자(a~z, A~Z), 숫자(0~9), 특수문자로만 구성되어야 합니다.")); // 오류 메시지 검증
        }

        @Test
        @DisplayName("특수 문자가 없을 때")
        void test5() {
            signupRequestDto.setPassword("Dlckdgud112345");
            Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(signupRequestDto); // 유효성 검사 수행
            assertTrue(violations.iterator().next().getMessage().contains("password는 알파벳 대소문자(a~z, A~Z), 숫자(0~9), 특수문자로만 구성되어야 합니다.")); // 오류 메시지 검증
        }
    }

    @Nested
    @DisplayName("회원가입 이메일 오류 테스트")
    class EmailTest{
        @Test
        @DisplayName("이메일 형식이 아닐 때")
        void test1() {
            signupRequestDto.setEmail("shlee509natecom");
            Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(signupRequestDto); // 유효성 검사 수행
            System.out.println(violations.iterator().next().getMessage());
            assertTrue(violations.iterator().next().getMessage().contains("올바른 형식의 이메일 주소여야 합니다")); // 오류 메시지 검증
        }

        @Test
        @DisplayName("이메일이 비었을 때")
        void test2() {
            signupRequestDto.setEmail("");
            Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(signupRequestDto); // 유효성 검사 수행
            assertTrue(violations.iterator().next().getMessage().contains("공백일 수 없습니다")); // 오류 메시지 검증
        }
    }

    @Nested
    @DisplayName("회원가입 회원 한 마디 테스트")
    class DescriptionTest{
        @Test
        @DisplayName("회원 한 마디 비었을 때")
        void test1() {
            signupRequestDto.setDescription("");
            Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(signupRequestDto); // 유효성 검사 수행
            assertTrue(violations.iterator().next().getMessage().contains("공백일 수 없습니다")); // 오류 메시지 검증
        }
    }
}
