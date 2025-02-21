package com.colon.mattfolio.database.token.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.colon.mattfolio.database.token.entity.TokenEntity;

@Repository
public interface TokenRepository extends CrudRepository<TokenEntity, String> {

    Optional<TokenEntity> findByAccessToken(String accessToken);
}
