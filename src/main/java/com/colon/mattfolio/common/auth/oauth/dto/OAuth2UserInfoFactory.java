package com.colon.mattfolio.common.auth.oauth.dto;

import java.util.Map;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if ("google".equalsIgnoreCase(registrationId)) {
            return parseGoogleUserInfo(attributes);
        } else if ("kakao".equalsIgnoreCase(registrationId)) {
            return parseKakaoUserInfo(attributes);
        } else if ("naver".equalsIgnoreCase(registrationId)) {
            return parseNaverUserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
        }
    }

    private static OAuth2UserInfo parseGoogleUserInfo(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String profile = (String) attributes.get("picture");
        return new DefaultOAuth2UserInfo("google", email, name, profile);
    }

    @SuppressWarnings("unchecked")
    private static OAuth2UserInfo parseKakaoUserInfo(Map<String, Object> attributes) {
        // 카카오의 경우, "kakao_account" 내에 실제 정보가 있습니다.
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = (String) kakaoAccount.get("email");
        Map<String, Object> profileMap = (Map<String, Object>) kakaoAccount.get("profile");
        String name = (String) profileMap.get("nickname");
        String profile = (String) profileMap.get("profile_image_url");
        return new DefaultOAuth2UserInfo("kakao", email, name, profile);
    }

    @SuppressWarnings("unchecked")
    private static OAuth2UserInfo parseNaverUserInfo(Map<String, Object> attributes) {
        // 네이버의 경우, 응답은 "response" 키 아래에 존재합니다.
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        String email = (String) response.get("email");
        String name = (String) response.get("name");
        String profile = (String) response.get("profile_image");
        return new DefaultOAuth2UserInfo("naver", email, name, profile);
    }
}
