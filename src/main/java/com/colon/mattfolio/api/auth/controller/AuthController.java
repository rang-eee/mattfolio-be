package com.colon.mattfolio.api.auth.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.colon.mattfolio.api.auth.dto.LoginResponse;
import com.colon.mattfolio.model.common.ApiResultDto;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// @CrossOrigin(origins = "http://about:blank")
@RestController("/auth")
@RequiredArgsConstructor
public class AuthController {

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

    @GetMapping("/success")
    public ResponseEntity<LoginResponse> loginSuccess(@Valid LoginResponse loginResponse) {
        return ResponseEntity.ok(loginResponse);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails userDetails) {
        // tokenService.deleteRefreshToken(userDetails.getUsername());
        // redisMessageService.removeSubscribe(userDetails.getUsername());
        return ResponseEntity.noContent()
            .build();
    }

}