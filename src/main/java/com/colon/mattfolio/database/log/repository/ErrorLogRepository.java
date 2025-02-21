package com.colon.mattfolio.database.log.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.colon.mattfolio.database.log.entiry.ErrorLogEntity;

public interface ErrorLogRepository extends JpaRepository<ErrorLogEntity, Long> {

    Optional<ErrorLogEntity> findByEmail(String email);

    Optional<ErrorLogEntity> findByMemberKey(String memberKey);
}