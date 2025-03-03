package com.colon.mattfolio.common.auth.oauth.dto;

import java.util.Map;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import com.colon.mattfolio.common.enumType.LoginAuthProvider;

/**
 * OAuth2UserInfoFactory 클래스는 OAuth2 인증 공급자별 사용자 정보를 파싱하여<br/>
 * 공통 DTO인 OAuth2UserInfo 객체로 생성하는 팩토리 클래스입니다.
 */
public class OAuth2UserInfoFactory {

    /**
     * registrationId와 attributes Map을 받아서 해당 공급자에 맞는 OAuth2UserInfo 객체를 생성합니다. <br/>
     * 
     * @param registrationId 인증 공급자 ID (예: "google", "kakao", "naver") <br/>
     * @param attributes 공급자로부터 전달받은 사용자 속성 Map <br/>
     * @return OAuth2UserInfo 객체 <br/>
     * @throws OAuth2AuthenticationException 지원되지 않는 공급자인 경우 예외 발생 <br/>
     */
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

    /**
     * Google 공급자의 사용자 정보를 파싱하여 OAuth2UserInfo 객체로 생성합니다. <br/>
     * 
     * @param attributes Google에서 전달받은 사용자 속성 Map <br/>
     * @return Google 사용자 정보를 담은 OAuth2UserInfo 객체 <br/>
     */
    private static OAuth2UserInfo parseGoogleUserInfo(Map<String, Object> attributes) {
        String providerId = (String) attributes.get("id"); // Google 사용자 고유 ID <br/>
        String email = (String) attributes.get("email"); // 사용자 이메일 <br/>
        String name = (String) attributes.get("name"); // 사용자 이름 <br/>
        String profile = (String) attributes.get("picture"); // 프로필 이미지 URL <br/>

        return OAuth2UserInfo.builder()
            .provider(LoginAuthProvider.GOOGLE)
            .providerId(providerId)
            .email(email)
            .name(name)
            .profile(profile)
            .build();
    }

    /**
     * Kakao 공급자의 사용자 정보를 파싱하여 OAuth2UserInfo 객체로 생성합니다. <br/>
     * 
     * @param attributes Kakao에서 전달받은 사용자 속성 Map <br/>
     * @return Kakao 사용자 정보를 담은 OAuth2UserInfo 객체 <br/>
     */
    @SuppressWarnings("unchecked")
    private static OAuth2UserInfo parseKakaoUserInfo(Map<String, Object> attributes) {
        // 카카오의 경우, 사용자 정보는 "kakao_account" 내부에 존재합니다. <br/>
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String providerId = String.valueOf(kakaoAccount.get("id")); // 공급자에서 제공하는 사용자 고유 ID<br/>
        String email = (String) kakaoAccount.get("email"); // 사용자 이메일 <br/>
        Map<String, Object> profileMap = (Map<String, Object>) kakaoAccount.get("profile");
        String name = (String) profileMap.get("nickname"); // 사용자 닉네임 (이름) <br/>
        String profile = (String) profileMap.get("profile_image_url"); // 프로필 이미지 URL <br/>

        return OAuth2UserInfo.builder()
            .provider(LoginAuthProvider.KAKAO)
            .providerId(providerId)
            .email(email)
            .name(name)
            .profile(profile)
            .build();
    }

    /**
     * Naver 공급자의 사용자 정보를 파싱하여 OAuth2UserInfo 객체로 생성합니다. <br/>
     * 
     * @param attributes Naver에서 전달받은 사용자 속성 Map <br/>
     * @return Naver 사용자 정보를 담은 OAuth2UserInfo 객체 <br/>
     */
    @SuppressWarnings("unchecked")
    private static OAuth2UserInfo parseNaverUserInfo(Map<String, Object> attributes) {
        // 네이버의 경우, 응답 데이터는 "response" 키 아래에 존재합니다. <br/>
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        String providerId = (String) response.get("id"); // 사용자 고유 ID <br/>
        String email = (String) response.get("email"); // 사용자 이메일 <br/>
        String name = (String) response.get("name"); // 사용자 이름 <br/>
        String profile = (String) response.get("profile_image"); // 프로필 이미지 URL <br/>

        return OAuth2UserInfo.builder()
            .provider(LoginAuthProvider.NAVER)
            .providerId(providerId)
            .email(email)
            .name(name)
            .profile(profile)
            .build();
    }
}
