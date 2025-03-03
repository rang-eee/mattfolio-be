package com.colon.mattfolio.database.token.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.colon.mattfolio.database.token.entity.AccountTokenEntity;

@Repository
public interface TokenRepository extends CrudRepository<AccountTokenEntity, Long> {

    Optional<AccountTokenEntity> findByAccessToken(String accessToken);

    Optional<AccountTokenEntity> findByRefreshToken(String refreshToken);

    Optional<AccountTokenEntity> findByUserId(Long userId);
}
