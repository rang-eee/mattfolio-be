package com.colon.mattfolio.common.auth.jwt;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.time.Duration;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.colon.mattfolio.common.exception.ErrorCode;
import com.colon.mattfolio.common.exception.TokenException;
import com.colon.mattfolio.database.account.entity.AccountEntity;
import com.colon.mattfolio.database.account.entity.AccountTokenEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * TokenProvider 클래스는 JWT 토큰을 생성, 검증, 파싱하는 역할을 수행합니다. <br/>
 * - Access Token 및 Refresh Token 생성 기능 <br/>
 * - JWT 토큰으로부터 인증 정보를 추출하여 Spring Security Authentication 객체 생성 <br/>
 * - 토큰 재발급 로직 포함
 */
@RequiredArgsConstructor
@Component
public class TokenProvider {

    // application.properties나 application.yml에서 주입받은 JWT 서명에 사용할 비밀 키
    @Value("${jwt.key}")
    private String key;

    // SecretKey 객체로 변환된 비밀 키 (PostConstruct에서 초기화)
    private SecretKey secretKey;

    // Access Token 만료 시간 (30분)
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30L;
    // Refresh Token 만료 시간 (7일)
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60L * 24 * 7;

    // JWT Claim에 저장할 역할(Role) 정보를 나타내는 키
    private static final String KEY_ROLE = "role";
    // JWT Claim에 저장할 공급자(provider) 정보를 나타내는 키
    private static final String KEY_PROVIDER = "provider";

    // 토큰 관련 비즈니스 로직을 처리하는 TokenService 빈 (토큰 저장, 업데이트 등)
    private final TokenService tokenService;

    /**
     * PostConstruct 메서드로, 주입받은 key를 기반으로 SecretKey 객체를 생성합니다.
     */
    @PostConstruct
    private void setSecretKey() {
        secretKey = Keys.hmacShaKeyFor(key.getBytes());
    }

    /**
     * Authentication 객체를 기반으로 Access Token을 생성합니다.
     * 
     * @param authentication Spring Security의 Authentication 객체
     * @return 생성된 Access Token 문자열
     */
    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, ACCESS_TOKEN_EXPIRE_TIME);
    }

    /**
     * Authentication 객체를 기반으로 Refresh Token을 생성하고 저장합니다.
     * 
     * @param authentication Spring Security의 Authentication 객체
     * @param accessToken 기존 Access Token 문자열 (토큰 재발급 시 사용)
     */
    public void generateRefreshToken(Authentication authentication, String accessToken) {
        // Refresh Token 생성
        String refreshToken = generateToken(authentication, REFRESH_TOKEN_EXPIRE_TIME);
        // authentication.getName()은 고유 사용자 식별자로 가정 (문자열이므로 Long으로 변환)
        Long authName = Long.parseLong(authentication.getName());
        // TokenService를 사용하여 Refresh Token과 기존 Access Token을 저장 또는 업데이트
        tokenService.saveOrUpdate(authName, refreshToken, accessToken);
    }

    /**
     * Authentication 객체와 만료 시간을 기반으로 JWT 토큰을 생성하는 내부 메서드. 토큰에는 subject(사용자 식별자), 역할 정보, 발급 시간, 만료 시간이 포함됩니다.
     * 
     * @param authentication Spring Security Authentication 객체
     * @param expireTime 토큰 만료 시간 (밀리초 단위)
     * @return 생성된 JWT 토큰 문자열
     */
    private String generateToken(Authentication authentication, long expireTime) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expireTime);

        // 인증 객체에 포함된 권한(역할) 정보를 문자열로 변환
        String authorities = authentication.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining());

        // JWT 빌더를 사용하여 토큰 생성
        return Jwts.builder()
            .subject(authentication.getName()) // 사용자 식별자
            .claim(KEY_ROLE, authorities) // 권한(역할) 정보 추가
            .issuedAt(now) // 토큰 발급 시간
            .expiration(expiredDate) // 토큰 만료 시간
            .compact();
    }

    /**
     * 주어진 JWT 토큰을 파싱하여 Spring Security Authentication 객체를 생성합니다.
     * 
     * @param token JWT 토큰 문자열
     * @return Authentication 객체 (UserDetails 기반)
     */
    public Authentication getAuthentication(String token) {
        // JWT 토큰에서 Claim 정보를 파싱
        Claims claims = parseClaims(token);
        // Claim에 저장된 역할 정보를 SimpleGrantedAuthority 리스트로 변환
        List<SimpleGrantedAuthority> authorities = getAuthorities(claims);
        // Spring Security User 객체를 생성 (비밀번호는 필요 없으므로 빈 문자열 사용)
        User principal = new User(claims.getSubject(), "", authorities);
        // UsernamePasswordAuthenticationToken 객체에 인증 정보를 담아 반환
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * Claims 객체에서 저장된 역할 정보를 추출하여, SimpleGrantedAuthority의 리스트로 변환합니다.
     * 
     * @param claims JWT Claims 객체
     * @return SimpleGrantedAuthority 리스트
     */
    private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        return Collections.singletonList(new SimpleGrantedAuthority(claims.get(KEY_ROLE)
            .toString()));
    }

    /**
     * 주어진 Access Token을 기반으로 Refresh Token을 재발급합니다. Refresh Token이 유효하면 새로운 Access Token을 생성하여 업데이트하고 반환합니다.
     * 
     * @param accessToken 기존 Access Token 문자열
     * @return 재발급된 Access Token 문자열 (유효하지 않으면 null 반환)
     */
    public String reissueAccessToken(String accessToken) {
        if (StringUtils.hasText(accessToken)) {
            // 기존 Access Token에 해당하는 토큰 엔티티 조회 (존재하지 않으면 예외 발생)
            AccountTokenEntity token = tokenService.findByAccessTokenOrThrow(accessToken);
            String refreshToken = token.getRefreshToken();

            // Refresh Token이 유효하면 재발급 진행
            if (validateToken(refreshToken)) {
                String reissueAccessToken = generateAccessToken(getAuthentication(refreshToken));
                tokenService.updateToken(reissueAccessToken, token);
                return reissueAccessToken;
            }
        }
        return null;
    }

    /**
     * 주어진 JWT 토큰의 유효성을 검증합니다. 토큰이 존재하고, 만료 시간이 현재 시간 이후면 유효한 토큰으로 간주합니다.
     * 
     * @param token JWT 토큰 문자열
     * @return 토큰이 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        Claims claims = parseClaims(token);
        return claims.getExpiration()
            .after(new Date());
    }

    /**
     * JWT 토큰을 파싱하여 Claims 객체를 반환합니다. 토큰이 만료된 경우 ExpiredJwtException에서 Claims를 추출하여 반환하고, 토큰 형식이 잘못되었거나 서명이 유효하지 않으면 TokenException을 발생시킵니다.
     * 
     * @param token JWT 토큰 문자열
     * @return 파싱된 Claims 객체
     * @throws TokenException 잘못된 토큰 형식 또는 유효하지 않은 서명일 경우
     */
    private Claims parseClaims(String token) {
        try {
            // secretKey를 사용하여 JWT를 검증하고, Claims 객체를 파싱
            return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우, 예외에서 Claims 객체를 추출하여 반환
            return e.getClaims();
        } catch (MalformedJwtException e) {
            throw new TokenException(ErrorCode.INVALID_TOKEN);
        } catch (SecurityException e) {
            throw new TokenException(ErrorCode.INVALID_JWT_SIGNATURE);
        }
    }

    /**
     * AccountEntity와 토큰 유효 기간(Duration)을 기반으로 JWT 토큰을 생성합니다. 토큰에는 사용자의 이메일, 역할, 공급자 정보 등이 포함됩니다.
     * 
     * @param account AccountEntity 객체
     * @param duration 토큰 유효 기간 (Duration 객체)
     * @return 생성된 JWT 토큰 문자열
     */
    public String generateToken(AccountEntity account, Duration duration) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + duration.toMillis());

        return Jwts.builder()
            .subject(account.getEmail()) // subject에 사용자 이메일 설정
            .claim(KEY_ROLE, account.getRole()
                .name()) // 역할 정보 추가
            .claim(KEY_PROVIDER, account.getLoginAuthProvider()
                .name()) // 공급자 정보 추가
            .issuedAt(now) // 토큰 발급 시간
            .expiration(expiredDate) // 토큰 만료 시간
            .compact();
    }
}