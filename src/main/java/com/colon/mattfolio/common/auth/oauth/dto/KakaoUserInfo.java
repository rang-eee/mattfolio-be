package com.colon.mattfolio.common.auth.oauth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoUserInfo {
    private Long id;
    private KakaoAccount kakaoAccount;

    @Getter
    @Builder
    public static class KakaoAccount {
        private String email;
        private Profile profile;

        @Getter
        @Builder
        public static class Profile {
            private String nickname;
            private String profileImageUrl;
        }
    }
}