package com.colon.mattfolio.common.auth;

import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.colon.mattfolio.common.enumType.LoginAuthProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthUtil {
    @Value("app.auth.token-secret")
    private String secret;

    private static final Long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 2L; // 2 hours
    private static final Long REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 30L; // 30 days

    public String createAccessToken(String userId, LoginAuthProvider provider, String accessToken) {
        HashMap<String, Object> claim = new HashMap<>();
        claim.put("userId", userId);
        claim.put("provider", provider);
        claim.put("accessToken", accessToken);
        return createJwt("ACCESS_TOKEN", ACCESS_TOKEN_EXPIRATION_TIME, claim);
    }

    public String createRefreshToken(String userId, LoginAuthProvider provider, String refreshToken) {
        HashMap<String, Object> claim = new HashMap<>();
        claim.put("userId", userId);
        claim.put("provider", provider);
        claim.put("refreshToken", refreshToken);
        return createJwt("REFRESH_TOKEN", REFRESH_TOKEN_EXPIRATION_TIME, claim);
    }

    public String createJwt(String subject, Long expiration, HashMap<String, Object> claim) {
        JwtBuilder jwtBuilder = Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setSubject(subject)
            .setIssuedAt(new Date())
            .signWith(SignatureAlgorithm.HS256, secret);

        if (claim != null) {
            jwtBuilder.setClaims(claim);
        }

        if (expiration != null) {
            jwtBuilder.setExpiration(new Date(new Date().getTime() + expiration));
        }

        return jwtBuilder.compact();
    }

    /**
     * 복호화
     */
    @SuppressWarnings("deprecation")
    public Claims get(String jwt) throws JwtException {
        return Jwts.parser() // JwtParserBuilder 반환
            .setSigningKey(secret) // 비밀 키 설정 (필요에 따라 byte[]로 변환 권장)
            .build() // JwtParser 객체 생성
            .parseClaimsJws(jwt) // JWT 파싱
            .getBody(); // Claims 반환
    }

    /**
     * 토큰 만료 여부 체크
     *
     * @return true : 만료됨, false : 만료되지 않음
     */
    public boolean isExpiration(String jwt) throws JwtException {
        try {
            return get(jwt).getExpiration()
                .before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Refresh token refresh 여부 확인 만료일 7일 이내 일 경우 refresh token 재발급
     */
    public boolean canRefresh(String refreshToken) throws JwtException {
        Claims claims = get(refreshToken);
        long expirationTime = claims.getExpiration()
            .getTime();
        long weekTime = 1000 * 60 * 60 * 24 * 7L;

        return new Date().getTime() > (expirationTime - weekTime);
    }
}
