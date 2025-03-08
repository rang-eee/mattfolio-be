package com.colon.mattfolio.api.auth.controller;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.colon.mattfolio.api.auth.dto.SignInRequest;
import com.colon.mattfolio.api.auth.dto.SignInResponse;
import com.colon.mattfolio.api.auth.service.AuthService;
import com.colon.mattfolio.common.base.MasterController;
import com.colon.mattfolio.common.exception.AuthException;
import com.colon.mattfolio.model.common.ApiResultDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController extends MasterController {

    private final AuthService authService;

    @GetMapping("/login/oauth2/code/{registrationId}")
    public ApiResultDto<SignInResponse> getExamplesByCriteriaPaging(//
            @PathVariable("registrationId") String registrationId, //
            @RequestParam("code") String code, //
            @RequestParam(value = "state", required = false) String state) {
        ApiResultDto<SignInResponse> apiResultVo = new ApiResultDto<>(); // API 응답 객체

        SignInResponse result; // 조회 결과

        try {
            SignInRequest request = SignInRequest.builder()
                .registrationId(registrationId)
                .code(code)
                .state(state)
                .build();

            // 서비스 호출을 통해 조회 결과 얻기
            result = authService.siginin(request);

            // 조회 결과가 있는 경우에만 데이터 설정
            apiResultVo.setData(result);
        } catch (AuthException e) {
            // 예제 예외 발생 시, 예외 코드 및 메시지 설정
            apiResultVo.setResultException(e);
        }

        return apiResultVo; // 최종 API 응답 반환
    }

    // @GetMapping("/login/oauth2/code/{registrationId}")
    // public ResponseEntity<SignInResponse> redirect(//
    // @PathVariable("registrationId") String registrationId, //
    // @RequestParam("code") String code, //
    // @RequestParam(value = "state", required = false) String state) {
    // try {
    // return ResponseEntity.ok(authService.redirect(RefreshTokenRequest.builder()
    // .registrationId(registrationId)
    // .code(code)
    // .state(state)
    // .build()));
    // } catch (BadRequestException e) {
    // e.printStackTrace();
    // return ResponseEntity.ok(SignInResponse.builder()
    // .accessToken(null)
    // .refreshToken(null)
    // .build());
    // }
    // }

    @GetMapping("/auth/success")
    public ResponseEntity<SignInResponse> loginSuccess(@Valid SignInResponse loginResponse) {
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/auth/token")
    public ResponseEntity<SignInResponse> refreshToken(@RequestBody SignInRequest tokenRequest) {
        try {
            return ResponseEntity.ok(authService.refreshToken(tokenRequest));
        } catch (BadRequestException e) {
            e.printStackTrace();
            return ResponseEntity.ok(SignInResponse.builder()
                .accessToken(null)
                .refreshToken(null)
                .build());
        }

    }
}