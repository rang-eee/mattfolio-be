package com.colon.mattfolio.api.auth.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.colon.mattfolio.model.common.ApiResultDto;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

// @CrossOrigin(origins = "http://about:blank")
@RestController("/v1/api/auth")
@RequiredArgsConstructor
public class Oauth2LoginController {

    final Log logger = LogFactory.getLog(getClass());

    @PostMapping("/signin")
    @Operation(summary = "Login", description = "Login")
    public ApiResultDto<String> signin() {
        ApiResultDto<String> apiResultVo = new ApiResultDto<>(); // API 응답 객체

        // 기본 성공 메시지 설정
        apiResultVo.setResultMessage("common.proc.success.search");

        // 조회 결과가 비어있는 경우 실패 메시지 설정
        apiResultVo.setData(null);
        apiResultVo.setResultMessage("common.proc.failed.search.empty");
        return apiResultVo; // 최종 API 응답 반환
    }

}