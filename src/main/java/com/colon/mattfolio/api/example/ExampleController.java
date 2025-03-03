package com.colon.mattfolio.api.example;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.colon.mattfolio.common.annotation.RoleUser;
import com.colon.mattfolio.common.base.MasterController;
import com.colon.mattfolio.model.common.ApiResultDto;

import lombok.RequiredArgsConstructor;

// @RequestMapping("/v1/api/example")
@RequiredArgsConstructor
@RestController
public class ExampleController extends MasterController {

	// @RoleUser
	@GetMapping("/api/example")
	// public ApiResultDto<String> memberInfo(@AuthenticationPrincipal UserDetails userDetails) {
	public ApiResultDto<String> memberInfo() {
		ApiResultDto<String> apiResultVo = new ApiResultDto<>(); // API 응답 객체

		// 기본 성공 메시지 설정
		apiResultVo.setResultMessage("common.proc.success.search");

		// 조회 결과가 비어있는 경우 실패 메시지 설정
		apiResultVo.setData(null);
		apiResultVo.setResultMessage("common.proc.failed.search.empty");
		return apiResultVo; // 최종 API 응답 반환
	}

	@RoleUser
	@GetMapping("/v1/api/example/detail")
	public ApiResultDto<String> getAccountDetail(@AuthenticationPrincipal UserDetails userDetails) {
		ApiResultDto<String> apiResultVo = new ApiResultDto<>(); // API 응답 객체

		// 기본 성공 메시지 설정
		apiResultVo.setResultMessage("common.proc.success.search");

		// 조회 결과가 비어있는 경우 실패 메시지 설정
		apiResultVo.setData(null);
		apiResultVo.setResultMessage("common.proc.failed.search.empty");
		return apiResultVo; // 최종 API 응답 반환
	}

}
