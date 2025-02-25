package com.colon.mattfolio.database.account.entity;

import com.colon.mattfolio.api.account.dto.MemberEditRequest;
import com.colon.mattfolio.database.common.BaseTimeEntity;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "mf_account") // 원하는 테이블명 지정
public class AccountEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(nullable = false, length = 10)
    private String name;

    @Column(nullable = false, length = 20, unique = true)
    private String email;

    @Column(nullable = false)
    private String profile;

    @Column(nullable = false, unique = true)
    private String memberKey;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Embedded
    private AddressEntity address;

    @Builder
    public AccountEntity(String name, String email, String profile, String memberKey, Role role) {
        this.name = name;
        this.email = email;
        this.profile = profile;
        this.memberKey = memberKey;
        this.role = role;
    }

    public void addAddress(AddressEntity address) {
        this.address = address;
    }

    public void updateMember(MemberEditRequest request) {
        this.name = request.name();

        if (request.address() != null) {
            this.address = request.address()
                .toEntity();
        }
    }

    public AccountEntity update(String name) {
        this.name = name;
        return this;
    }
}
