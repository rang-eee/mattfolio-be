package com.colon.mattfolio.database.common;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * BaseTimeEntity 클래스는 모든 엔티티에 공통적으로 적용되는 생성/수정 시간 및 생성/수정 계정 정보를 관리하기 위한 MappedSuperclass입니다.<br/>
 * Auditing 기능을 통해 엔티티가 생성되거나 수정될 때 자동으로 날짜와 관련 계정 정보를 업데이트합니다.<br/>
 * 클라이언트에 노출되지 않도록 JsonIgnore로 마스킹 처리합니다.<br/>
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    // 엔티티가 생성된 시간을 자동으로 기록합니다. <br/>
    @JsonIgnore
    @CreatedDate
    @Column(name = "CREATED_DATE")
    protected LocalDateTime createdDate;

    // 엔티티 생성 시 사용한 계정의 ID를 저장합니다. <br/>
    @JsonIgnore
    @Column(name = "CREATED_ACCOUNT_ID")
    protected Long createdAccountId;

    // 엔티티가 마지막으로 수정된 시간을 자동으로 기록합니다. <br/>
    @JsonIgnore
    @LastModifiedDate
    @Column(insertable = false, name = "MODIFIED_DATE")
    protected LocalDateTime modifiedDate;

    // 엔티티 수정 시 사용한 계정의 ID를 저장합니다. <br/>
    @JsonIgnore
    @Column(name = "MODIFIED_ACCOUNT_ID")
    protected Long modifiedAccountId;
}
