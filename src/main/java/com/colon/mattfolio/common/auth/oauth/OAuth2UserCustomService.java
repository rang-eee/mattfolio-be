package com.colon.mattfolio.common.auth.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.colon.mattfolio.common.auth.oauth.dto.OAuth2UserInfo;
import com.colon.mattfolio.common.auth.oauth.dto.OAuth2UserInfoFactory;
import com.colon.mattfolio.database.account.entity.AccountEntity;
import com.colon.mattfolio.database.account.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    private final AccountRepository accountRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 공급자 식별 (registrationId: "kakao", "google", "naver")
        String registrationId = userRequest.getClientRegistration()
            .getRegistrationId();

        // 공통 팩토리를 통해 공급자별 OAuth2UserInfo 객체 생성
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());
        saveOrUpdate(userInfo);
        return oAuth2User;
    }

    // 유저가 있으면 업데이트, 없으면 생성
    private AccountEntity saveOrUpdate(OAuth2UserInfo userInfo) {
        String email = userInfo.getEmail();
        String name = userInfo.getName();

        AccountEntity user = accountRepository.findByEmail(email)
            .map(entity -> entity.update(name))
            .orElse(AccountEntity.builder()
                .email(email)
                .name(name)
                .build());

        return accountRepository.save(user);
    }
}
