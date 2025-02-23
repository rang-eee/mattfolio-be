package com.colon.mattfolio.database.token.repository;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.WebUtils;

import com.colon.mattfolio.util.CookieUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class OAuth2AuthorizationRequestBasedOnCookieRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

	// OAuth2 인증 요청을 저장할 쿠키의 이름
	public final static String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";

	// 쿠키의 만료 시간
	private final static int COOKIE_EXPIRE_SECONDS = 18000;

	// 요청을 제거
	// 실제로 쿠키에서 요청을 제거하지 않고, 단순히 요청을 로드하여 반환
	@Override
	public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
		return this.loadAuthorizationRequest(request);
	}

	// 쿠키에서 OAuth2 인증 요청을 로드
	// 쿠키 값을 역직렬화하여 OAuth2AuthorizationRequest 객체로 변환
	@Override
	public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
		Cookie cookie = WebUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
		return CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class);
	}

	// OAuth2 인증 요청을 쿠키에 저장
	// CookieUtil.addCookie를 사용하여 요청을 직렬화한 후 쿠키에 저장
	@Override
	public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
		if (authorizationRequest == null) {
			removeAuthorizationRequestCookies(request, response);
			return;
		}

		CookieUtil.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, CookieUtil.serialize(authorizationRequest), COOKIE_EXPIRE_SECONDS);
	}

	public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
		CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
	}
}