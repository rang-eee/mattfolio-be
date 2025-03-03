package com.colon.mattfolio.common.auth.jwt;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.colon.mattfolio.common.exception.ErrorCode;
import com.colon.mattfolio.common.exception.TokenException;
import com.colon.mattfolio.database.token.entity.AccountTokenEntity;
import com.colon.mattfolio.database.token.repository.TokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    public void deleteRefreshToken(Long memberKey) {
        tokenRepository.deleteById(memberKey);
    }

    @Transactional
    public void saveOrUpdate(Long memberKey, String refreshToken, String accessToken) {
        AccountTokenEntity token = tokenRepository.findByAccessToken(accessToken)
            .map(o -> o.updateRefreshToken(refreshToken))
            .orElseGet(() -> new AccountTokenEntity(memberKey, refreshToken, accessToken));

        tokenRepository.save(token);
    }

    public AccountTokenEntity findByAccessTokenOrThrow(String accessToken) {
        return tokenRepository.findByAccessToken(accessToken)
            .orElseThrow(() -> new TokenException(ErrorCode.TOKEN_EXPIRED));
    }

    @Transactional
    public void updateToken(String accessToken, AccountTokenEntity token) {
        token.updateAccessToken(accessToken);
        tokenRepository.save(token);
    }
}
