package com.colon.mattfolio.api.auth.dto;

import java.util.Map;

import com.colon.mattfolio.common.exception.ErrorCode;
import com.colon.mattfolio.database.account.entity.AccountEntity;
import com.colon.mattfolio.util.KeyGenerator;

import lombok.Builder;

@Builder
public record OAuth2UserInfo(String name, String email, String profile) {

    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
        case "google" -> ofGoogle(attributes);
        case "kakao" -> ofKakao(attributes);
        default -> throw new com.colon.mattfolio.common.exception.AuthException(ErrorCode.ILLEGAL_REGISTRATION_ID);
        };
    }

    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
            .name((String) attributes.get("name"))
            .email((String) attributes.get("email"))
            .profile((String) attributes.get("picture"))
            .build();
    }

    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        return OAuth2UserInfo.builder()
            .name((String) profile.get("nickname"))
            .email((String) account.get("email"))
            .profile((String) profile.get("profile_image_url"))
            .build();
    }

    public AccountEntity toEntity() {
        return AccountEntity.builder()
            .name(name)
            .email(email)
            .profile(profile)
            .memberKey(KeyGenerator.generateKey())
            .role(com.colon.mattfolio.database.account.entity.Role.USER)
            .build();
    }
}
