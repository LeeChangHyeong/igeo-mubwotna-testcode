package com.sparta.igeomubwotna.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j(topic = "JWT 관련 로그")
@Component
public class JwtUtil {
    // AccessToken KEY 값 (이름)
    public static final String ACCESS_HEADER = "Authorization";

    // 사용자 아이디 값의 KEY (이름)
    public static final String ACCESS_USERID = "X-User-Id";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    // 토큰 만료시간
    private final long ACCESS_TOKEN_TIME = 30 * 60 * 1000L; // 30분
    private final long REFRESH_TOKEN_TIME = 14 * 24 * 60 * 60 * 1000L; // 2주

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // 딱 한 번만 받아오면 되는 값을 사용할 때마다 요청을 새로고침하는 오류를 방지하기 위해
    @PostConstruct
    public void init() {
        // Base64로 디코딩
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // AccessToken 생성
    // TODO: 나중에 user에 상태가 필요하면 말씀해 주세요!
    public String createAccessToken(String userId) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(userId) // 유저 식별 값
                        .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_TIME)) // 만료 시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
//                        .claim() // 상태
    }

    // RefreshToken 생성
    public String createRefreshToken(String userId) {
        Date date = new Date();

        return Jwts.builder()
                .setSubject(userId)
                .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_TIME))
                .setIssuedAt(date)
                .signWith(key, signatureAlgorithm)
                .compact();

    }

    // AccessToken을 header에서 가져와서 반환하는 메서드
    public String getAccessTokenFromHeader(HttpServletRequest request) {
        String accessToken = request.getHeader(ACCESS_HEADER);
        if (StringUtils.hasText(accessToken) && accessToken.startsWith(BEARER_PREFIX)) {
            return accessToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    // UserId를 header에서 가져와서 반환하는 메서드
    public String getUserIdFromHeader(HttpServletRequest request) {
        String userId = request.getHeader(ACCESS_USERID);

        if (StringUtils.hasText(userId)) {
            return userId;
        }
        return null;
    }

    // Access 토큰 검증
    public boolean validateAccessToken(String accessToken, String refreshToken, HttpServletResponse response) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid AccessToken signature, 유효하지 않는 AccessToken 서명 입니다.");
        } catch (ExpiredJwtException e) {
            refreshAccessToken(refreshToken, response);
            log.error("Expired AccessToken token, 만료된 AccessToken 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported AccessToken token, 지원되지 않는 AccessToken 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("AccessToken claims is empty, 잘못된 AccessToken 토큰 입니다.");
        }
        return false;
    }

    // refresh 토큰 검증
    public boolean validateRefreshToken(String refreshToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(refreshToken);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid RefreshToken, 유효하지 않는 RefreshToken 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired RefreshToken, 만료된 RefreshToken 입니다. 다시 로그인 해주세요.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported RefreshToken, 지원되지 않는 RefreshToken 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("RefreshToken claims is empty, 잘못된 RefreshToken 입니다.");
        }
        return false;
    }

    // RefreshToken 검증 및 AccessToken 재발급
    public String refreshAccessToken(String refreshToken, HttpServletResponse response) {
        if (validateRefreshToken(refreshToken)) {
            Claims claims = getUserInfoFromToken(refreshToken);
            String userId = claims.getSubject();
            String newToken = createAccessToken(userId);

            response.setHeader(ACCESS_HEADER, newToken);

            return newToken;
        }
        return null;
    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}