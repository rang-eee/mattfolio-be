package com.colon.mattfolio.api.auth.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.colon.mattfolio.api.auth.dto.LoginRequest;
import com.colon.mattfolio.api.auth.dto.LoginResponse;
import com.colon.mattfolio.api.auth.service.AuthService;
import com.colon.mattfolio.common.base.MasterController;
import com.colon.mattfolio.common.dto.ApiResultDto;
import com.colon.mattfolio.common.exception.AuthException;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController extends MasterController {

    private final AuthService authService;

    @GetMapping("/login/{registrationId}")
    public ApiResultDto<LoginResponse> getExamplesByCriteriaPaging(//
            @PathVariable("registrationId") String registrationId, //
            @RequestParam("code") String code, //
            @RequestParam(value = "state", required = false) String state) {
        ApiResultDto<LoginResponse> apiResultVo = new ApiResultDto<>(); // API 응답 객체

        LoginResponse result; // 조회 결과

        try {
            LoginRequest request = LoginRequest.builder()
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
            throw e;
        }

        return apiResultVo; // 최종 API 응답 반환
    }

    // @PostMapping(value = "/signup/{registrationId}/{code}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // public ApiResultDto<LoginResponse> uploadFiles( //
    // @Parameter(description = "업로드할 파일들", required = true) @RequestParam("files") MultipartFile[] files, //
    // @PathVariable("registrationId") String registrationId, //
    // @PathVariable("code") String code) {
    // ApiResultDto<LoginResponse> apiResultVo = new ApiResultDto<>();

    // LoginResponse result; // 조회 결과

    // try {
    // LoginRequest request = LoginRequest.builder()
    // .registrationId(registrationId)
    // .code(code)
    // .files(files)
    // .isSignup(true)
    // .build();

    // // 서비스 호출을 통해 조회 결과 얻기
    // result = authService.signup(request);

    // // 조회 결과가 있는 경우에만 데이터 설정
    // apiResultVo.setData(result);
    // } catch (AuthException e) {
    // // 예제 예외 발생 시, 예외 코드 및 메시지 설정
    // throw e;
    // }

    // return apiResultVo;
    // }
}