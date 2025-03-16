package com.colon.mattfolio.database.account.entity;

import com.colon.mattfolio.api.account.dto.MemberEditRequest;
import com.colon.mattfolio.common.enumType.AccountRoleType;
import com.colon.mattfolio.common.enumType.AccountStatusType;
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

/**
 * AccountEntity 클래스는 MF_ACCOUNT 테이블에 매핑되는 엔티티로, 사용자 계정 정보를 저장합니다. <br/>
 * BaseTimeEntity를 상속하여 생성일, 수정일 등 공통 필드를 포함합니다. <br/>
 */
@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MF_ACCOUNT")
public class AccountEntity extends BaseTimeEntity {

    // 계정의 고유 ID (PRIMARY KEY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    // 로그인 인증 공급자 (예: GOOGLE, KAKAO 등)
    @Enumerated(EnumType.STRING)
    @Column(name = "LOGIN_AUTH_PROVIDER", nullable = true)
    private LoginAuthProvider loginAuthProvider;

    // 공급자로부터 제공받은 사용자 고유 ID
    @Column(name = "LOGIN_AUTH_PROVIDER_ID", nullable = false, length = 100)
    private String loginAuthProviderId;

    // 사용자 이메일
    @Column(name = "EMAIL", nullable = true, length = 50)
    private String email;

    // 사용자 이름
    @Column(name = "NAME", nullable = true, length = 100)
    private String name;

    // 사용자 프로필 이미지 URL
    @Column(name = "PROFILE", nullable = true)
    private String profileImgUrl;

    // 사용자 상태
    @Column(name = "STATUS", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountStatusType status;

    // 사용자 역할 (예: USER, ADMIN 등)
    @Column(name = "ROLE", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountRoleType role;

    // 주석 처리된 필드들 (필요 시 주석 해제)
    // @Column(name = "NICKNAME", nullable = true, length = 30)
    // private String nickname;

    // @Column(name = "GENDER", nullable = true)
    // private String gender;

    // @Column(name = "AGE", nullable = true)
    // private Integer age;

    /**
     * MemberEditRequest의 정보를 반영하여 사용자 이름을 업데이트합니다.
     * 
     * @param request 사용자 수정 요청 DTO
     */
    public void updateMember(MemberEditRequest request) {
        this.name = request.name();
    }

    /**
     * 주어진 이름으로 사용자 이름을 업데이트하고, 변경된 AccountEntity 객체를 반환합니다.
     * 
     * @param name 새 사용자 이름
     * @return 업데이트된 AccountEntity 객체
     */
    public AccountEntity update(String name) {
        this.name = name;
        return this;
    }
}
