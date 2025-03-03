package com.colon.mattfolio.database.account.entity;

import com.colon.mattfolio.api.account.dto.MemberEditRequest;
import com.colon.mattfolio.common.enumType.AccountRoleType;
import com.colon.mattfolio.common.enumType.LoginAuthProvider;
import com.colon.mattfolio.database.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MF_ACCOUNT")
public class AccountEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "AUTH_PROVIDER", nullable = true)
    private LoginAuthProvider authProvider;

    @Column(name = "PROVIDER_ID", nullable = false, length = 20)
    private String providerId;

    @Column(name = "EMAIL", nullable = true, length = 50)
    private String email;

    @Column(name = "NAME", nullable = true, length = 30)
    private String name;

    @Column(name = "NICKNAME", nullable = true, length = 30)
    private String nickname;

    @Column(name = "PROFILE", nullable = true)
    private String profileImgUrl;

    @Column(name = "GENDER", nullable = true)
    private String gender;

    @Column(name = "AGE", nullable = true)
    private Integer age;

    @Column(name = "ROLE", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountRoleType role;

    public void updateMember(MemberEditRequest request) {

        this.name = request.name();
    }

    public AccountEntity update(String name) {
        this.name = name;
        return this;
    }
}