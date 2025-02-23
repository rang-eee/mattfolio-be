package com.colon.mattfolio.api.auth.service;

import com.colon.mattfolio.database.account.entity.AccountEntity;
import com.colon.mattfolio.database.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

	private final AccountRepository accountRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User user = super.loadUser(userRequest); // ❶ 요청을 바탕으로 유저 정보를 담은 객체 반환
		saveOrUpdate(user);

		return user;
	}

	// 유저가 있으면 업데이트, 없으면 유저 생성
	private AccountEntity saveOrUpdate(OAuth2User oAuth2User) {
		Map<String, Object> attributes = oAuth2User.getAttributes();

		String email = getKakaoEmail(attributes);
		String name = getKakaoNickname(attributes);

		AccountEntity user = accountRepository.findByEmail(email)
			.map(entity -> entity.update(name))
			.orElse(AccountEntity.builder()
				.email(email)
				.name(name)
				.build());

		return accountRepository.save(user);

	}

	/*
	 * 카카오 api 응답 구조가 이렇게 됨 { "id": 1234567890, "connected_at": "2021-01-01T00:00:00Z", "properties": { "nickname": "홍길동", "profile_image": "http://example.com/profile.jpg", "thumbnail_image": "http://example.com/thumbnail.jpg" }, "kakao_account": { "email": "hong@example.com",
	 * "profile_needs_agreement": false, "profile": { "nickname": "홍길동", "thumbnail_image_url": "http://example.com/thumbnail.jpg", "profile_image_url": "http://example.com/profile.jpg", "is_default_image": false }, ... } }
	 */
	private String getKakaoEmail(Map<String, Object> attributes) {
		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
		return (String) kakaoAccount.get("email");
	}

	private String getKakaoNickname(Map<String, Object> attributes) {
		Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
		return (String) properties.get("nickname");
	}
}