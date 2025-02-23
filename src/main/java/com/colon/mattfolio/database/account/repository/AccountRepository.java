package com.colon.mattfolio.database.account.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.colon.mattfolio.database.account.entity.AccountEntity;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByEmail(String email);

    Optional<AccountEntity> findByMemberKey(String memberKey);

}
