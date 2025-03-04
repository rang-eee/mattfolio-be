package com.colon.mattfolio.database.account.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.colon.mattfolio.database.account.entity.AccountTokenEntity;

/**
 * AccountTokenRepository 인터페이스는 AccountTokenEntity에 대한 CRUD 작업을 수행하기 위해 CrudRepository를 확장합니다. <br/>
 * 이 인터페이스를 통해 토큰 데이터(Access Token, Refresh Token 등)의 조회, 저장, 삭제 등을 처리할 수 있습니다.
 */
@Repository
public interface AccountTokenRepository extends CrudRepository<AccountTokenEntity, Long> {

    /**
     * 주어진 Access Token을 기반으로 AccountTokenEntity를 조회합니다. <br/>
     * 
     * @param accessToken 조회할 Access Token
     * @return 해당 Access Token과 일치하는 AccountTokenEntity가 존재하면 Optional로 반환
     */
    Optional<AccountTokenEntity> findByAccessToken(String accessToken);

    /**
     * 주어진 Refresh Token을 기반으로 AccountTokenEntity를 조회합니다. <br/>
     * 
     * @param refreshToken 조회할 Refresh Token
     * @return 해당 Refresh Token과 일치하는 AccountTokenEntity가 존재하면 Optional로 반환
     */
    Optional<AccountTokenEntity> findByRefreshToken(String refreshToken);

    /**
     * 주어진 계정 ID를 기반으로 AccountTokenEntity를 조회합니다. <br/>
     * 
     * @param accountId 계정의 고유 ID
     * @return 해당 계정 ID와 일치하는 AccountTokenEntity가 존재하면 Optional로 반환
     */
    Optional<AccountTokenEntity> findByAccountId(Long accountId);
}
