// package com.colon.mattfolio.common.auth;

// import java.util.Collections;

// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.stereotype.Component;

// import com.colon.mattfolio.common.auth.jwt.TokenProvider;
// import com.colon.mattfolio.common.enumType.LoginAuthProvider;

// import lombok.RequiredArgsConstructor;

// @Component
// @RequiredArgsConstructor
// public class AuthUtil {

// private final TokenProvider tokenProvider;

// /**
// * 사용자 식별자와 공급자 정보를 바탕으로 Authentication을 생성하고, <br/>
// * 이를 이용해 AccessToken을 생성합니다.
// */
// public String createAccessToken(String userId, LoginAuthProvider provider, String providerAccessToken) {
// // 필요한 경우, provider나 providerAccessToken을 클레임에 추가할 수 있음
// Authentication authentication = new UsernamePasswordAuthenticationToken( //
// userId, //
// null, //
// Collections.emptyList() // 권한 정보가 있다면 여기에 추가
// );

// // SecurityContext에 등록 (선택 사항)
// SecurityContextHolder.getContext()
// .setAuthentication(authentication);

// // 생성한 Authentication 객체를 사용하여 Access Token 발급
// return tokenProvider.generateAccessToken(authentication);
// }

// /**
// * 사용자 식별자와 공급자 정보를 바탕으로 Authentication을 생성하고, <br/>
// * 이를 이용해 RefreshToken을 생성 및 저장합니다.
// */
// public String createRefreshToken(String userId, LoginAuthProvider provider, String providerRefreshToken) {
// Authentication authentication = new UsernamePasswordAuthenticationToken( //
// userId, //
// null, //
// Collections.emptyList()//
// );

// SecurityContextHolder.getContext()
// .setAuthentication(authentication);

// // RefreshToken 생성 및 저장 (tokenProvider 내부에서 처리)
// tokenProvider.generateRefreshToken(authentication, providerRefreshToken);
// return providerRefreshToken;
// }
// }
