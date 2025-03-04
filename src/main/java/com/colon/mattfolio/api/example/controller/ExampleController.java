package com.colon.mattfolio.api.example.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.colon.mattfolio.api.example.service.ExampleService;
import com.colon.mattfolio.common.annotation.RoleUser;
import com.colon.mattfolio.common.base.MasterController;
import com.colon.mattfolio.database.example.entity.ExampleEntity;
import com.colon.mattfolio.model.common.ApiResultDto;

import lombok.RequiredArgsConstructor;

/**
 * ExampleController 클래스는 예제 관련 API 엔드포인트를 제공하는 컨트롤러입니다. <br/>
 * - "/api/example" 엔드포인트를 통해 모든 예제 데이터를 조회합니다. <br/>
 * - "/v1/api/example/detail" 엔드포인트는 @RoleUser 어노테이션이 적용되어 인증된 사용자에 대한 상세 정보를 제공하도록 설계되었습니다.
 */
@RequiredArgsConstructor
@RestController
public class ExampleController extends MasterController {

    // 예제 데이터 조회에 필요한 서비스 객체
    private final ExampleService exampleService;

    /**
     * 모든 예제 데이터를 조회하는 API 엔드포인트입니다. <br/>
     * 기본적으로 성공 메시지를 설정하며, 조회 결과가 없으면 실패 메시지로 대체합니다. <br/>
     * GET 요청: /api/example <br/>
     * 
     * @return ApiResultDto 객체에 예제 데이터 리스트 또는 실패 메시지가 담겨 반환됩니다.
     */
    @GetMapping("/api/example")
    public ApiResultDto<List<ExampleEntity>> memberInfo() {
        ApiResultDto<List<ExampleEntity>> apiResultVo = new ApiResultDto<>(); // API 응답 객체 생성

        // 기본 성공 메시지 설정
        apiResultVo.setResultMessage("common.proc.success.search");

        // ExampleService를 사용하여 모든 예제 데이터를 조회
        List<ExampleEntity> result = exampleService.findAllExamples();

        if (result.isEmpty()) {
            // 조회 결과가 없으면 데이터는 null로 설정하고, 실패 메시지를 설정
            apiResultVo.setData(null);
            apiResultVo.setResultMessage("common.proc.failed.search.empty");
        } else {
            // 조회 결과가 있으면 데이터로 설정
            apiResultVo.setData(result);
        }

        return apiResultVo; // 최종 API 응답 반환
    }

    /**
     * 인증된 사용자에 대한 상세 정보를 조회하는 API 엔드포인트입니다. <br/>
     * 이 엔드포인트는 @RoleUser 어노테이션에 의해 인증된 사용자만 접근할 수 있습니다. <br/>
     * GET 요청: /v1/api/example/detail <br/>
     * 
     * @param userDetails 현재 인증된 사용자의 세부 정보를 담은 UserDetails 객체 (Spring Security에 의해 주입됨)
     * @return ApiResultDto 객체에 상세 정보(현재는 null)와 관련 메시지가 담겨 반환됩니다.
     */
    @RoleUser
    @GetMapping("/v1/api/example/detail")
    public ApiResultDto<String> getAccountDetail(@AuthenticationPrincipal UserDetails userDetails) {
        ApiResultDto<String> apiResultVo = new ApiResultDto<>(); // API 응답 객체 생성

        // 기본 성공 메시지 설정
        apiResultVo.setResultMessage("common.proc.success.search");

        // 현재 구현에서는 상세 정보를 조회하지 않고 null로 설정하여 실패 메시지로 대체함
        apiResultVo.setData(null);
        apiResultVo.setResultMessage("common.proc.failed.search.empty");
        return apiResultVo; // 최종 API 응답 반환
    }
}
