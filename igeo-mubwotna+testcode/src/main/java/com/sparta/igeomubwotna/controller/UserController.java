package com.sparta.igeomubwotna.controller;

import com.sparta.igeomubwotna.dto.*;
import com.sparta.igeomubwotna.security.UserDetailsImpl;
import com.sparta.igeomubwotna.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Response> signup(@RequestBody @Valid SignupRequestDto requestDto, BindingResult bindingResult) {
        // UserService의 signup 메서드에 데이터 넘겨 줌
        return userService.signup(requestDto, bindingResult);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUserProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 인증 객체에서 사용자 정보를 추출
        UserProfileDto userProfile = userService.getUserProfile(userDetails.getUser().getId());
        return ResponseEntity.ok(userProfile);
    }

    @PatchMapping("/me")
    public ResponseEntity<Response> updateUserProfile(@RequestBody @Valid UserUpdateRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 인증 객체에서 사용자 정보를 추출
        return userService.updateUserProfile(requestDto, userDetails.getUser().getId());
    }

    @PostMapping("/logout")
    public ResponseEntity<Response> logout(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.logout(userDetails.getUser().getId());
    }

    @PatchMapping("/withdraw")
    public ResponseEntity<Response> withdrawUser(@RequestBody PasswordDto passwordDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 인증 객체에서 사용자 정보를 추출
        return userService.withdrawUser(passwordDto, userDetails.getUser().getId());
    }
}
