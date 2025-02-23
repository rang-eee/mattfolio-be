package com.colon.mattfolio.api.auth.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.colon.mattfolio.common.jwt.TokenProvider;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/oauth2")
@AllArgsConstructor
public class OAuth2Controller {

	private final Log logger = LogFactory.getLog(getClass());

	private final TokenProvider tokenProvider;

	/**
	 * OAuth2 인증 요청을 처리하는 엔드포인트입니다. 클라이언트가 전달한 파라미터를 확인한 후, 더미 인증 코드를 생성하여 리다이렉트합니다.
	 */
	@GetMapping("/authorize")
	@Operation(summary = "OAuth2 Authorization Endpoint", description = "Simulates the OAuth2 authorization process and redirects with an authorization code.")
	public void authorize(@RequestParam(name = "response_type") String responseType, //
			@RequestParam(name = "client_id") String clientId, //
			@RequestParam(name = "redirect_uri") String redirectUri, //
			@RequestParam(name = "state", required = false) String state, //
			HttpServletResponse response) throws IOException {

		logger.info("Received OAuth2 authorization request: response_type=" + responseType + ", client_id=" + clientId);
		// 더미 인증 코드 생성
		String authorizationCode = "dummy_code";
		// redirect_uri에 code와 state 파라미터 추가
		StringBuilder redirectUrl = new StringBuilder(redirectUri);
		if (redirectUri.contains("?")) {
			redirectUrl.append("&");
		} else {
			redirectUrl.append("?");
		}
		redirectUrl.append("code=")
			.append(authorizationCode);
		if (state != null) {
			redirectUrl.append("&state=")
				.append(state);
		}
		response.sendRedirect(redirectUrl.toString());
	}

	@GetMapping("/signin")
	@Operation(summary = "OAuth2 Token Endpoint", description = "Issues a valid JWT access token based on a valid authorization code.")
	public ResponseEntity<Map<String, Object>> signin(@RequestParam Map<String, String> parameters) {
		logger.info("Received OAuth2 token request: " + parameters);
		String code = parameters.get("code");
		// 인증 코드 검증 로직 추가 (예: DB에 저장된 값과 비교)
		if (code == null || !code.equals("expected_code")) {
			return ResponseEntity.badRequest()
				.body(Collections.singletonMap("error", "invalid_grant"));
		}

		// 실제 사용자 인증 정보 획득 (예: OAuth2 인증 성공 후 생성된 Authentication 객체)
		// Authentication authentication = ...; // 사용자 인증 정보 생성 또는 조회
		// String accessToken = tokenProvider.generateAccessToken(authentication);
		// tokenProvider.generateRefreshToken(authentication, accessToken);

		Map<String, Object> tokenResponse = new HashMap<>();
		// tokenResponse.put("access_token", accessToken);
		tokenResponse.put("token_type", "bearer");
		tokenResponse.put("expires_in", 1800); // 예: 30분
		tokenResponse.put("scope", "any");
		return ResponseEntity.ok(tokenResponse);
	}

	@GetMapping("/token")
	@Operation(summary = "OAuth2 Token Endpoint", description = "Issues a valid JWT access token based on a valid authorization code.")
	public ResponseEntity<Map<String, Object>> token(@RequestParam Map<String, String> parameters) {
		logger.info("Received OAuth2 token request: " + parameters);
		String code = parameters.get("code");
		// 인증 코드 검증 로직 추가 (예: DB에 저장된 값과 비교)
		if (code == null || !code.equals("expected_code")) {
			return ResponseEntity.badRequest()
				.body(Collections.singletonMap("error", "invalid_grant"));
		}

		// 실제 사용자 인증 정보 획득 (예: OAuth2 인증 성공 후 생성된 Authentication 객체)
		// Authentication authentication = ...; // 사용자 인증 정보 생성 또는 조회
		// String accessToken = tokenProvider.generateAccessToken(authentication);
		// tokenProvider.generateRefreshToken(authentication, accessToken);

		Map<String, Object> tokenResponse = new HashMap<>();
		// tokenResponse.put("access_token", accessToken);
		tokenResponse.put("token_type", "bearer");
		tokenResponse.put("expires_in", 1800); // 예: 30분
		tokenResponse.put("scope", "any");
		return ResponseEntity.ok(tokenResponse);
	}

}
