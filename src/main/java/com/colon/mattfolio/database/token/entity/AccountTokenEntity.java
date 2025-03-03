package com.colon.mattfolio.database.token.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "MF_TOKEN")
public class AccountTokenEntity {

    @Id
    @Column(name = "ACCOUNT_ID", nullable = false)
    private Long accountId;

    @Column(name = "REFRESH_TOKEN", nullable = false)
    private String refreshToken;

    @Column(name = "ACCESS_TOKEN", nullable = false)
    private String accessToken;

    public AccountTokenEntity updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public void updateAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}

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
