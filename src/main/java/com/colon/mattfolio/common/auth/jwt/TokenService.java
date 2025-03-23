package com.colon.mattfolio.common.auth.jwt;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.colon.mattfolio.common.exception.ErrorCode;
import com.colon.mattfolio.common.exception.TokenException;
import com.colon.mattfolio.database.account.entity.AccountEntity;
import com.colon.mattfolio.database.account.entity.AccountTokenEntity;
import com.colon.mattfolio.database.account.repository.AccountTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TokenService 클래스는 JWT 토큰과 관련된 CRUD 및 갱신 비즈니스 로직을 담당합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class TokenService {

    // AccountTokenRepository를 통해 토큰 엔티티에 접근합니다.
    private final AccountTokenRepository tokenRepository;

    /**
     * 주어진 계정 ID에 해당하는 Refresh Token 정보를 삭제합니다.
     *
     * @param accountId 삭제할 계정의 고유 식별자
     */
    public void deleteRefreshToken(Long accountId) {
        tokenRepository.deleteById(accountId);
    }

    /**
     * 주어진 계정 정보와 토큰 값을 바탕으로 Refresh Token을 저장하거나 업데이트합니다.
     * <p>
     * 만약 accessToken에 해당하는 토큰 엔티티가 이미 존재하면 Refresh Token 값을 업데이트하며, <br/>
     * 존재하지 않으면 새로운 토큰 엔티티를 생성합니다.
     *
     * @param account 토큰과 연결된 계정 엔티티
     * @param refreshToken 새로 생성되거나 갱신된 Refresh Token 값
     * @param accessToken 현재 사용 중인 Access Token 값
     */
    @Transactional
    public void saveOrUpdate(AccountEntity account, String refreshToken, String accessToken) {
        AccountTokenEntity token = tokenRepository.findByAccessToken(accessToken)
            .map(existingToken -> existingToken.updateRefreshToken(refreshToken))
            .orElseGet(() -> AccountTokenEntity.builder()
                .accountId(account.getAccountId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());
        tokenRepository.save(token);
    }

    /**
     * 주어진 Access Token에 해당하는 토큰 엔티티를 조회합니다. <br/>
     * 해당 토큰 엔티티가 존재하지 않으면 TokenException을 발생시킵니다.
     *
     * @param accessToken 조회할 Access Token 값
     * @return 조회된 AccountTokenEntity
     * @throws TokenException 토큰 엔티티가 존재하지 않을 경우 예외 발생
     */
    public AccountTokenEntity findByAccessTokenOrThrow(String accessToken) {
        return tokenRepository.findByAccessToken(accessToken)
            .orElseThrow(() -> new TokenException(ErrorCode.TOKEN_EXPIRED));
    }

    /**
     * 주어진 토큰 엔티티의 Access Token 값을 새 값으로 갱신합니다.
     *
     * @param accessToken 새로 생성된 Access Token 값
     * @param token 갱신 대상인 토큰 엔티티
     */
    @Transactional
    public void updateToken(String accessToken, AccountTokenEntity token) {
        token.updateAccessToken(accessToken);
        tokenRepository.save(token);
    }
}
