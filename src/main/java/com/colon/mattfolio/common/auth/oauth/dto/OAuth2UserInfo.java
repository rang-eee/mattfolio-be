package com.colon.mattfolio.common.auth.oauth.dto;

import com.colon.mattfolio.common.enumType.LoginAuthProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * OAuth2UserInfo 클래스는 OAuth2 인증 공급자로부터 전달받은 사용자 정보를 담기 위한 DTO입니다. <br/>
 * 이 클래스는 사용자의 공급자 정보, 공급자 고유 ID, 이메일, 이름, 그리고 프로필(예: 프로필 이미지 URL)을 포함합니다. <br/>
 */
@Builder
@Getter
@AllArgsConstructor
public class OAuth2UserInfo {

    // 인증 공급자 정보를 나타냅니다. (예: GOOGLE, KAKAO, NAVER 등) <br/>
    private final LoginAuthProvider provider;

    // 공급자로부터 제공받은 고유 사용자 식별자입니다. <br/>
    private final String providerId;

    // 사용자의 이메일 주소입니다. <br/>
    private final String email;

    // 사용자의 이름입니다. <br/>
    private final String name;

    // 사용자의 프로필 URL (예: 프로필 이미지 URL)입니다. <br/>
    private final String profile;
}
