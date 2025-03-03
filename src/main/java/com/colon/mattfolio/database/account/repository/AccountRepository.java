package com.colon.mattfolio.database.account.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.colon.mattfolio.common.enumType.LoginAuthProvider;
import com.colon.mattfolio.database.account.entity.AccountEntity;

/**
 * AccountRepository 인터페이스는 AccountEntity에 대한 데이터 접근 계층을 제공합니다. <br/>
 * Spring Data JPA를 사용하여 기본 CRUD 및 사용자 정의 메서드를 자동으로 구현합니다.
 */
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    /**
     * 이메일을 기반으로 AccountEntity를 조회합니다. <br/>
     * 
     * @param email 검색할 이메일 주소
     * @return 해당 이메일을 가진 AccountEntity가 존재하면 Optional로 반환
     */
    Optional<AccountEntity> findByEmail(String email);

    /**
     * 로그인 인증 공급자와 공급자 ID를 기반으로 AccountEntity를 조회합니다. <br/>
     * 
     * @param authProvider 인증 공급자 (예: GOOGLE, KAKAO, NAVER 등)
     * @param providerId 해당 공급자에서 제공한 사용자 고유 ID
     * @return 해당 조건에 맞는 AccountEntity가 존재하면 Optional로 반환
     */
    Optional<AccountEntity> findByLoginAuthProviderAndLoginAuthProviderId(LoginAuthProvider authProvider, String providerId);

    /**
     * 로그인 인증 공급자와 공급자 ID를 기반으로 AccountEntity의 존재 여부를 확인합니다. <br/>
     * 
     * @param authProvider 인증 공급자 (예: GOOGLE, KAKAO, NAVER 등)
     * @param providerId 해당 공급자에서 제공한 사용자 고유 ID
     * @return 해당 조건에 맞는 AccountEntity가 존재하면 true, 그렇지 않으면 false
     */
    boolean existsByLoginAuthProviderAndLoginAuthProviderId(LoginAuthProvider authProvider, String providerId);
}
