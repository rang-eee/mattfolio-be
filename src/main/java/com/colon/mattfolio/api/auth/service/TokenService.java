package com.colon.mattfolio.api.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.colon.mattfolio.common.exception.ErrorCode;
import com.colon.mattfolio.common.exception.TokenException;
import com.colon.mattfolio.database.token.entity.TokenEntity;
import com.colon.mattfolio.database.token.repository.TokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    public void deleteRefreshToken(String memberKey) {
        tokenRepository.deleteById(memberKey);
    }

    @Transactional
    public void saveOrUpdate(String memberKey, String refreshToken, String accessToken) {
        TokenEntity token = tokenRepository.findByAccessToken(accessToken)
            .map(o -> o.updateRefreshToken(refreshToken))
            .orElseGet(() -> new TokenEntity(memberKey, refreshToken, accessToken));

        tokenRepository.save(token);
    }

    public TokenEntity findByAccessTokenOrThrow(String accessToken) {
        return tokenRepository.findByAccessToken(accessToken)
            .orElseThrow(() -> new TokenException(ErrorCode.TOKEN_EXPIRED));
    }

    @Transactional
    public void updateToken(String accessToken, TokenEntity token) {
        token.updateAccessToken(accessToken);
        tokenRepository.save(token);
    }
}
