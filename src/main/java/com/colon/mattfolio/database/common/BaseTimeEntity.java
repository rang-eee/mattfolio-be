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

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    @JsonIgnore
    @CreatedDate
    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    @JsonIgnore
    @Column(name = "CREATED_ACCOUNT_ID")
    private Long createdAccountId;

    @JsonIgnore
    @LastModifiedDate
    @Column(insertable = false, name = "MODIFIED_DATE")
    private LocalDateTime modifiedDate;

    @JsonIgnore
    @Column(name = "MODIFIED_ACCOUNT_ID")
    private Long modifiedAccountId;
}
