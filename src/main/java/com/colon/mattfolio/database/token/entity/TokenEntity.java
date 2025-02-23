package com.colon.mattfolio.database.token.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mf_token")
public class TokenEntity {

    @Id
    @Column(nullable = false)
    private String id;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private String accessToken;

    public TokenEntity updateRefreshToken(String refreshToken) {
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
