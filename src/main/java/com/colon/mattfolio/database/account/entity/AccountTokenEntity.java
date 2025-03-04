package com.colon.mattfolio.database.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AccountTokenEntity 클래스는 MF_TOKEN 테이블과 매핑되며, <br/>
 * 각 계정의 Access Token과 Refresh Token 정보를 저장합니다.
 */
@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MF_TOKEN")
public class AccountTokenEntity {

    /**
     * 계정의 고유 ID (PRIMARY KEY) <br/>
     */
    @Id
    @Column(name = "ACCOUNT_ID", nullable = false)
    private Long accountId;

    /**
     * 사용자의 Refresh Token <br/>
     */
    @Column(name = "REFRESH_TOKEN")
    private String refreshToken;

    /**
     * 사용자의 Access Token <br/>
     */
    @Column(name = "ACCESS_TOKEN")
    private String accessToken;

    /**
     * 주어진 Refresh Token으로 기존 Refresh Token 값을 업데이트합니다. <br/>
     * 
     * @param refreshToken 새로 갱신된 Refresh Token
     * @return 업데이트된 AccountTokenEntity 객체
     */
    public AccountTokenEntity updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    /**
     * 주어진 Access Token으로 기존 Access Token 값을 업데이트합니다. <br/>
     * 
     * @param accessToken 새로 갱신된 Access Token
     */
    public void updateAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}

/////////////////// Redis 사용 예제

// import jakarta.persistence.Id;
// import lombok.AllArgsConstructor;
// import lombok.Getter;
// import org.springframework.data.redis.core.RedisHash;
// import org.springframework.data.redis.core.index.Indexed;

// @Getter
// @AllArgsConstructor
// @RedisHash(value = "jwt", timeToLive = 60 * 60 * 24 * 7)
// public class Token {

// @Id
// private String id;

// private String refreshToken;

// @Indexed
// private String accessToken;

// public Token updateRefreshToken(String refreshToken) {
// this.refreshToken = refreshToken;
// return this;
// }

// public void updateAccessToken(String accessToken) {
// this.accessToken = accessToken;
// }
// }
