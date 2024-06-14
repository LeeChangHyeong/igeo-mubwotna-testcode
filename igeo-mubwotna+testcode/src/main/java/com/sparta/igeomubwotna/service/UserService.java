package com.sparta.igeomubwotna.service;

import com.sparta.igeomubwotna.dto.*;
import com.sparta.igeomubwotna.entity.User;
import com.sparta.igeomubwotna.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ResponseEntity<Response> signup(SignupRequestDto requestDto, BindingResult bindingResult) {
        ResponseEntity<Response> returnError = checkError("회원가입에 실패하였습니다.", bindingResult);

        if (returnError != null) {
            return returnError;
        }

        String userId = requestDto.getUserId();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 회원 userId 중복 확인
        Optional<User> checkUserId = userRepository.findByUserId(userId);
        if (checkUserId.isPresent()) {
            // 오류 메시지와 상태 코드 반환
            Response response = new Response(HttpStatus.BAD_REQUEST.value(), "중복된 아이디가 존재합니다.");

            return ResponseEntity.badRequest().body(response);
        }

        // email 중복 확인
        String email = requestDto.getEmail();
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            // 오류 메시지와 상태 코드 반환
            Response response = new Response(HttpStatus.BAD_REQUEST.value(), "중복된 이메일이 존재합니다.");

            return ResponseEntity.badRequest().body(response);
        }

        // 사용자 등록
        User user = new User(userId, password, requestDto.getName(), requestDto.getEmail(), requestDto.getDescription());
        userRepository.save(user);

        // 성공 메시지와 상태 코드 반환
        Response response = new Response(HttpStatus.OK.value(), "회원가입에 성공하였습니다.");
        return ResponseEntity.ok(response);
    }

    // 클라이언트에서 입력받아 오는 값을 유효성 검사하는 로직
    public ResponseEntity<Response> checkError(String message, BindingResult bindingResult) {
        // 유효성 검사 예외 처리
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        // 유효성 검사에 오류가 있으면
        if (fieldErrors.size() > 0) {
            // 모든 필드 오류에 대해 로그 기록 및 오류 메시지 수집
            List<String> errorMessages = new ArrayList<>();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                String errorMessage = fieldError.getField() + " 필드: " + fieldError.getDefaultMessage();
                log.error(errorMessage);
                errorMessages.add(errorMessage);
            }
            Response response = new Response(HttpStatus.BAD_REQUEST.value(), message);
            return ResponseEntity.badRequest().body(response);
        }

        return null;
    }

    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile(Long userId) {
        // ID로 사용자를 검색하고, 없으면 예외를 던짐
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 사용자의 프로필을 찾을 수 없습니다."));
        return new UserProfileDto(user);
    }

    @Transactional
    public ResponseEntity<Response> updateUserProfile(UserUpdateRequestDto requestDto, Long userId) {
        // ID로 사용자를 검색하고, 없으면 예외를 던짐
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 사용자의 프로필을 찾을 수 없습니다."));

        if (requestDto.getName() != null) {
            user.updateName(requestDto.getName());
        }
        if (requestDto.getDescription() != null) {
            user.updateDescription(requestDto.getDescription());
        }
        if (requestDto.getNewPassword() != null) {
            // 입력한 현재 비밀번호가 올바른지 확인
            if (!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
                Response response = new Response(HttpStatus.BAD_REQUEST.value(), "입력한 현재 비밀번호가 일치하지 않습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            // 새로운 비밀번호가 현재 비밀번호와 같은지 확인
            if (passwordEncoder.matches(requestDto.getNewPassword(), user.getPassword())) {
                Response response = new Response(HttpStatus.BAD_REQUEST.value(), "새로운 비밀번호는 현재 비밀번호와 달라야 합니다.");
                return ResponseEntity.badRequest().body(response);
            }
            // 새로운 비밀번호 설정
            user.updatePassword(passwordEncoder.encode(requestDto.getNewPassword()));
        }
        userRepository.save(user);  // 사용자 정보 저장

        Response response = new Response(HttpStatus.OK.value(), "프로필 정보를 성공적으로 수정하였습니다.");
        return ResponseEntity.ok().body(response);
    }

    @Transactional
    public ResponseEntity<Response> logout(Long userId) {
        // ID로 사용자를 검색하고, 없으면 예외를 던짐
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."));

        // refreshToken 초기화
        user.updateRefreshToken(null);

        // 사용자 정보 저장
        userRepository.save(user);

        // SecurityContextHolder 초기화
        SecurityContextHolder.clearContext();

        Response response = new Response(HttpStatus.OK.value(), "로그아웃이 완료되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Response> withdrawUser(PasswordDto passwordDto, Long userId) {
        // ID로 사용자를 검색하고, 없으면 예외를 던짐
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."));

        // 입력받은 비밀번호를 인코딩하여 기존 비밀번호와 비교
        if (!passwordEncoder.matches(passwordDto.getPassword(), user.getPassword())) {
            Response response = new Response(HttpStatus.BAD_REQUEST.value(), "비밀번호가 일치하지 않습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        // 회원 탈퇴 처리
        user.withdraw();
        userRepository.save(user);

        Response response = new Response(HttpStatus.OK.value(), "회원 탈퇴가 성공적으로 완료되었습니다.");
        return ResponseEntity.ok().body(response);
    }

    public User findById(Long recipeId) {
        return userRepository.findById(recipeId).orElseThrow(() ->
                new IllegalArgumentException("해당 사용자가 존재하지 않습니다.")
        );
    }
}
