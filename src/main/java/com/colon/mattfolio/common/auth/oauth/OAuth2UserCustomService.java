package com.colon.mattfolio.common.auth.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.colon.mattfolio.common.auth.oauth.dto.OAuth2UserInfo;
import com.colon.mattfolio.common.auth.oauth.dto.OAuth2UserInfoFactory;
import com.colon.mattfolio.common.enumType.AccountRoleType;
import com.colon.mattfolio.common.enumType.LoginAuthProvider;
import com.colon.mattfolio.database.account.entity.AccountEntity;
import com.colon.mattfolio.database.account.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

/**
 * OAuth2UserCustomService 클래스는 OAuth2 인증 요청을 처리하여 공급자별 사용자 정보를 공통 DTO(OAuth2UserInfo)로 변환하고, 기존 계정 정보를 업데이트하거나 새 계정을 생성하는 역할을 합니다.<br/>
 */
@Service
@RequiredArgsConstructor
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    // AccountRepository를 통해 계정 정보를 DB에서 조회 및 저장합니다.
    private final AccountRepository accountRepository;

    /**
     * OAuth2UserRequest를 기반으로 사용자 정보를 로드합니다. 기본 DefaultOAuth2UserService의 loadUser 메서드를 호출한 후, 공급자(registrationId)를 통해 OAuth2UserInfo 객체를 생성하고 계정 정보를 업데이트 또는 생성합니다.<br/>
     * 
     * @param userRequest OAuth2 인증 요청 객체
     * @return 로드된 OAuth2User 객체
     * @throws OAuth2AuthenticationException 인증 과정 중 오류 발생 시 예외 발생
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 공급자 식별 (registrationId: "kakao", "google", "naver")
        String registrationId = userRequest.getClientRegistration()
            .getRegistrationId();

        // 공통 팩토리를 통해 공급자별 OAuth2UserInfo 객체 생성
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());
        this.saveOrUpdate(userInfo);
        return oAuth2User;
    }

    /**
     * 공급자별 사용자 정보(OAuth2UserInfo)를 기반으로 계정을 조회하여 업데이트하거나, 새로 생성합니다.<br/>
     * 
     * @param userInfo 공급자별로 파싱된 사용자 정보 DTO
     * @return 저장 또는 업데이트된 AccountEntity 객체
     */
    private AccountEntity saveOrUpdate(OAuth2UserInfo userInfo) {
        LoginAuthProvider provider = userInfo.getProvider(); // 인증 공급자
        String providerId = userInfo.getProviderId(); // 공급자에서 제공한 사용자 고유 ID
        String email = userInfo.getEmail(); // 사용자 이메일
        String name = userInfo.getName(); // 사용자 이름

        AccountEntity user = accountRepository.findByLoginAuthProviderAndLoginAuthProviderId(provider, providerId)
            .map(accountEntity -> accountEntity.update(name))
            .orElse(AccountEntity.builder() // 새 계정 생성
                .loginAuthProvider(provider)
                .loginAuthProviderId(providerId)
                .email(email)
                .name(name)
                .role(AccountRoleType.USER)
                .build());

        return accountRepository.save(user);
    }
}
