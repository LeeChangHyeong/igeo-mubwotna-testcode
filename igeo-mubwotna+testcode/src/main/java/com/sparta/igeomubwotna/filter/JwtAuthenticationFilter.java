package com.sparta.igeomubwotna.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.igeomubwotna.dto.SigninRequestDto;
import com.sparta.igeomubwotna.jwt.JwtUtil;
import com.sparta.igeomubwotna.repository.UserRepository;
import com.sparta.igeomubwotna.security.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    // 로그인 시도 처리
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // 요청에서 로그인 정보를 읽어와 DTO에 매핑
            SigninRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), SigninRequestDto.class);

            // 인증 매니저를 통해 사용자 인증 시도
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getUserId(),  // 사용자 아이디
                            requestDto.getPassword(),  // 비밀번호
                            null  // 권한 목록은 null로 전달
                    )
            );
        } catch (IOException e) {
            // 예외 발생 시 로그 출력 및 RuntimeException 던지기
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    // 로그인 성공 시 처리
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        // 사용자 아이디와 역할 정보를 가져옴
        String userId = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
//        UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();

        if (userRepository.findByUserId(userId).get().isWithdrawn()) {
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("이미 탈퇴한 회원입니다.");  // 탈퇴한 사용자는 로그인 못함

            return;
        }

        // AccessToken 생성
        String accessToken = jwtUtil.createAccessToken(userId);
        // 응답 헤더에 AccessToken 추가
        response.addHeader(JwtUtil.ACCESS_HEADER, accessToken);
        // 응답 헤더에 userId 추가
        response.addHeader(JwtUtil.ACCESS_USERID, userId);

        // RefreshToken 생성
        String refreshToken = jwtUtil.createRefreshToken(userId);

        // 로그인시 RefreshToken을 user DB에 저장
        userRepository.findByUserId(userId).ifPresent(
                user -> {
                    user.updateRefreshToken(refreshToken);
                    userRepository.save(user);
                }
        );

        // 한국어 쓰기위해 인코딩
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("로그인 성공.");
    }

    // 로그인 실패 시 처리
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(401);

        // 한국어 쓰기위해 인코딩
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("로그인 실패.");
    }
}