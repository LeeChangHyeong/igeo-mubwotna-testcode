package com.sparta.igeomubwotna.filter;

import com.sparta.igeomubwotna.entity.User;
import com.sparta.igeomubwotna.jwt.JwtUtil;
import com.sparta.igeomubwotna.repository.UserRepository;
import com.sparta.igeomubwotna.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        String url = req.getRequestURI();

        if (StringUtils.hasText(url) && (url.equals("/api/user/signin") || url.equals("/api/user/signup") || url.startsWith("/swagger"))) {
            filterChain.doFilter(req, res);
            return; // 필터 체인을 빠져나갑니다.
        }

        // HTTP 요청에서 UserId 추출
        String userId = jwtUtil.getUserIdFromHeader(req);

        Optional<User> userOptional = userRepository.findByUserId(userId);

        // 사용자가 존재하지 않는 경우 또는 사용자가 탈퇴한 경우 처리
        if (userOptional.isEmpty() || userOptional.get().isWithdrawn()) {
            if (url.startsWith("/api")) { // API 요청인 경우에만 응답을 보냄
                res.setCharacterEncoding("UTF-8");
                res.getWriter().write("이미 탈퇴한 회원이거나 존재하지 않는 회원입니다.");  // 탈퇴한 사용자 또는 존재하지 않는 사용자는 로그인 못함
                return;
            }
        }

        // HTTP 요청에서 Access 토큰 추출
        String accessToken = jwtUtil.getAccessTokenFromHeader(req);

        if (userOptional.isPresent()) {
            // 유저 정보로 refreshToken 들고오기
            String refreshToken = userOptional.get().getRefreshToken();

            if (refreshToken == null) {
                res.setCharacterEncoding("UTF-8");
                res.getWriter().write("다시 로그인해주세요.");  // 로그아웃하여 Refresh Token이 초기화되었으므로 재로그인 유도
                return;
            }

            if (StringUtils.hasText(accessToken)) {
                // Access 토큰 유효성 검증
                if (!jwtUtil.validateAccessToken(accessToken, refreshToken, res)) {
                    // 유효하지 않은 토큰이면 에러 로깅 후 종료
                    return;
                }
                // JWT 토큰으로부터 사용자 정보(Claims) 추출
                Claims info = jwtUtil.getUserInfoFromToken(accessToken);

                try {
                    // 사용자 인증 처리
                    setAuthentication(info.getSubject());
                } catch (Exception e) {
                    // 인증 처리 중 예외 발생 시 에러 로깅 후 종료
                    log.error(e.getMessage());
                    return;
                }
            }
        }

        // 다음 필터(또는 서블릿)로 요청 전달
        filterChain.doFilter(req, res);
    }

    // 사용자 인증 처리
    public void setAuthentication(String username) {
        // 빈 SecurityContext 생성
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // 사용자의 인증 객체 생성
        Authentication authentication = createAuthentication(username);

        // SecurityContext에 인증 객체 설정
        context.setAuthentication(authentication);

        // SecurityContext를 SecurityContextHolder에 설정
        SecurityContextHolder.setContext(context);
    }

    // 사용자의 인증 객체 생성
    private Authentication createAuthentication(String username) {
        // 사용자 정보 조회
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        // UserDetails를 사용하여 인증 토큰 생성
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}