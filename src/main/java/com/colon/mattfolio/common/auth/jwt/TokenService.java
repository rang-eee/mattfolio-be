package com.colon.mattfolio.common.auth.jwt;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.colon.mattfolio.common.exception.ErrorCode;
import com.colon.mattfolio.common.exception.TokenException;
import com.colon.mattfolio.database.account.entity.AccountTokenEntity;
import com.colon.mattfolio.database.account.repository.AccountTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TokenService 클래스는 토큰의 CRUD 및 갱신 관련 비즈니스 로직을 담당합니다. <br/>
 * - Refresh Token 삭제, 저장/업데이트, 조회, 및 Access Token 갱신 기능을 제공합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class TokenService {

    // AccountTokenRepository를 통해 토큰 엔티티에 접근합니다.
    private final AccountTokenRepository tokenRepository;

    /**
     * 주어진 memberKey에 해당하는 Refresh Token을 삭제합니다.
     * 
     * @param memberKey 삭제할 계정의 고유 식별자
     */
    public void deleteRefreshToken(Long memberKey) {
        tokenRepository.deleteById(memberKey);
    }

    /**
     * 주어진 memberKey, refreshToken, accessToken 정보를 바탕으로 토큰을 저장하거나 업데이트합니다.
     * 
     * @param memberKey 계정의 고유 식별자
     * @param refreshToken 새로 생성된 Refresh Token
     * @param accessToken 현재의 Access Token
     */
    @Transactional
    public void saveOrUpdate(Long memberKey, String refreshToken, String accessToken) {
        // accessToken을 기준으로 기존 토큰 엔티티를 조회하고, 있으면 refreshToken 업데이트, 없으면 새로 생성합니다. <br/>
        AccountTokenEntity token = tokenRepository.findByAccessToken(accessToken)
            .map(accountTokenEntity -> accountTokenEntity.updateRefreshToken(refreshToken))
            .orElseGet(() -> AccountTokenEntity.builder()
                .accountId(memberKey)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());
        // 토큰 엔티티를 저장합니다.
        tokenRepository.save(token);
    }

    /**
     * 주어진 Access Token을 기준으로 토큰 엔티티를 조회합니다. <br/>
     * 만약 토큰이 존재하지 않으면 TokenException을 발생시킵니다.
     * 
     * @param accessToken 조회할 Access Token
     * @return 조회된 AccountTokenEntity
     * @throws TokenException 토큰이 만료되었거나 존재하지 않을 경우
     */
    public AccountTokenEntity findByAccessTokenOrThrow(String accessToken) {
        return tokenRepository.findByAccessToken(accessToken)
            .orElseThrow(() -> new TokenException(ErrorCode.TOKEN_EXPIRED));
    }

    /**
     * 주어진 Access Token으로 토큰 엔티티를 갱신합니다.
     * 
     * @param accessToken 새로 생성된 Access Token
     * @param token 갱신할 토큰 엔티티
     */
    @Transactional
    public void updateToken(String accessToken, AccountTokenEntity token) {
        token.updateAccessToken(accessToken);
        tokenRepository.save(token);
    }
}
