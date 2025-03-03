package com.colon.mattfolio.common.auth.oauth.dto;

public interface OAuth2UserInfo {
    String getEmail();

    String getName();

    String getProvider();

    String getProfile(); // 프로필 정보 (예: 프로필 이미지 URL)

}
