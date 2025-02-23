// package com.colon.mattfolio.api.auth.service;

// import java.util.Map;

// import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
// import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
// import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
// import org.springframework.security.oauth2.core.user.OAuth2User;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import com.colon.mattfolio.api.auth.dto.OAuth2UserInfo;
// import com.colon.mattfolio.api.auth.dto.model.PrincipalDetails;
// import com.colon.mattfolio.database.account.entity.AccountEntity;
// import com.colon.mattfolio.database.account.repository.AccountRepository;

// import lombok.RequiredArgsConstructor;

// @RequiredArgsConstructor
// @Service
// public class CustomOAuth2UserService extends DefaultOAuth2UserService {

// private final AccountRepository accountRepository;

// @Transactional
// @Override
// public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
// Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();
// String registrationId = userRequest.getClientRegistration()
// .getRegistrationId();
// String userNameAttributeName = userRequest.getClientRegistration()
// .getProviderDetails()
// .getUserInfoEndpoint()
// .getUserNameAttributeName();

// OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, oAuth2UserAttributes);
// AccountEntity member = getOrSave(oAuth2UserInfo);

// return new PrincipalDetails(member, oAuth2UserAttributes, userNameAttributeName);
// }

// private AccountEntity getOrSave(OAuth2UserInfo oAuth2UserInfo) {
// AccountEntity member = accountRepository.findByEmail(oAuth2UserInfo.email())
// .orElseGet(oAuth2UserInfo::toEntity);
// return accountRepository.save(member);
// }
// }
