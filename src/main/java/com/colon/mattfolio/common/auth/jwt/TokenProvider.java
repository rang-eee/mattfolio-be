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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.colon.mattfolio.common.enumType.LoginAuthProvider;
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
 * - 토큰 재발급 로직 포함 <br/>
 * 
 * 기존 AuthUtil에서 제공하던 사용자 ID, 공급자 정보를 기반으로 한 토큰 생성 기능을 createAccessToken()과 createRefreshToken() 메서드로 통합하였습니다.
 */
@RequiredArgsConstructor
@Component
public class TokenProvider {

    @Value("${jwt.key}")
    private String key;

    private SecretKey secretKey;

    // Access Token 만료 시간 (30분)
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30L;
    // Refresh Token 만료 시간 (7일)
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60L * 24 * 7;

    private final TokenService tokenService;

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
    public String generateRefreshToken(Authentication authentication, String accessToken) {
        String refreshToken = generateToken(authentication, REFRESH_TOKEN_EXPIRE_TIME);
        AccountEntity accountPricipal = (AccountEntity) authentication.getPrincipal();
        tokenService.saveOrUpdate(accountPricipal, refreshToken, accessToken);
        return refreshToken;
    }

    /**
     * Authentication 객체와 만료 시간을 기반으로 JWT 토큰을 생성하는 내부 메서드.
     *
     * @param authentication Spring Security Authentication 객체
     * @param expireTime 토큰 만료 시간 (밀리초 단위)
     * @return 생성된 JWT 토큰 문자열
     */
    private String generateToken(Authentication authentication, long expireTime) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expireTime);

        AccountEntity account = (AccountEntity) authentication.getPrincipal();

        return Jwts.builder()
            .subject(String.valueOf(account.getAccountId()))
            .claim("email", account.getEmail())
            .claim("provider", account.getLoginAuthProvider())
            .claim("providerId", account.getLoginAuthProviderId())
            .issuedAt(now)
            .expiration(expiredDate)
            .signWith(secretKey)
            .compact();
    }

    /**
     * JWT 토큰을 파싱하여 Authentication 객체를 생성합니다.
     *
     * @param token JWT 토큰 문자열
     * @return 생성된 Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        User principal = new User(claims.getSubject(), "", List.of());
        return new UsernamePasswordAuthenticationToken(principal, token, List.of());
    }

    /**
     * Access Token을 기반으로 Refresh Token의 유효성을 확인 후, 새로운 Access Token을 발급합니다.
     *
     * @param accessToken 기존 Access Token 문자열
     * @return 재발급된 Access Token 문자열 (유효하지 않으면 null)
     */
    public String reissueAccessToken(String accessToken) {
        if (StringUtils.hasText(accessToken)) {
            AccountTokenEntity token = tokenService.findByAccessTokenOrThrow(accessToken);
            String refreshToken = token.getRefreshToken();
            if (validateToken(refreshToken)) {
                String reissueAccessToken = generateAccessToken(getAuthentication(refreshToken));
                tokenService.updateToken(reissueAccessToken, token);
                return reissueAccessToken;
            }
        }
        return null;
    }

    /**
     * 주어진 JWT 토큰의 유효성을 검증합니다.
     *
     * @param token JWT 토큰 문자열
     * @return 유효하면 true, 아니면 false
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
     * JWT 토큰을 파싱하여 Claims 객체를 반환합니다.
     *
     * @param token JWT 토큰 문자열
     * @return 파싱된 Claims 객체
     * @throws TokenException 잘못된 토큰 형식 또는 유효하지 않은 서명인 경우
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (MalformedJwtException e) {
            throw new TokenException(ErrorCode.INVALID_TOKEN);
        } catch (SecurityException e) {
            throw new TokenException(ErrorCode.INVALID_JWT_SIGNATURE);
        }
    }

    // ========================================================================================
    // 아래의 메서드들은 기존 AuthUtil의 기능을 통합한 메서드로,
    // 사용자 ID 및 공급자 정보를 기반으로 토큰을 생성할 수 있도록 합니다.
    // ========================================================================================

    /**
     * 사용자 ID와 공급자 정보를 기반으로 Authentication 객체를 생성하고, 이를 이용해 Access Token을 생성합니다.
     *
     * @param userId 사용자 식별자 (예: 카카오톡 ID)
     * @param provider 로그인 인증 공급자 (예: KAKAO)
     * @param providerAccessToken 공급자에서 발급받은 Access Token (필요시 클레임에 추가 가능)
     * @return 생성된 Access Token 문자열
     */
    public String createAccessToken(AccountEntity account, LoginAuthProvider provider, String providerAccessToken) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(//
                account, //
                null, //
                Collections.emptyList() //
        );
        SecurityContextHolder.getContext()
            .setAuthentication(authentication);

        return generateAccessToken(authentication);
    }

    /**
     * 사용자 ID와 공급자 정보를 기반으로 Authentication 객체를 생성하고, 이를 이용해 Refresh Token을 생성 및 저장합니다.
     *
     * @param userId 사용자 식별자 (예: 카카오톡 ID)
     * @param provider 로그인 인증 공급자 (예: KAKAO)
     * @param providerRefreshToken 공급자에서 발급받은 Refresh Token
     * @return 공급자에서 받은 Refresh Token (내부적으로 저장 처리됨)
     */
    public String createRefreshToken(AccountEntity account, LoginAuthProvider provider, String accessToken) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(//
                account, //
                null, //
                Collections.emptyList()//
        );

        SecurityContextHolder.getContext()
            .setAuthentication(authentication);

        String refreshToken = generateRefreshToken(authentication, accessToken);
        return refreshToken;
    }
}
