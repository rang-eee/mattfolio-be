package com.colon.mattfolio.api.dummy.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.colon.mattfolio.api.dummy.service.DummyService;
import com.colon.mattfolio.common.base.MasterController;
import com.colon.mattfolio.common.dto.ApiResultDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class DummyController extends MasterController {

    private final DummyService dummyService;

    @GetMapping("/api/dummy/festival")
    public ApiResultDto<Map<String, Object>> findAllFestival( //
            @RequestParam(defaultValue = "1") Integer page, //
            @RequestParam(defaultValue = "20") Integer pageSize //
    ) {
        ApiResultDto<Map<String, Object>> apiResultVo = new ApiResultDto<>(); // API 응답 객체 생성

        Map<String, Object> result = dummyService.findAllFestival(page, pageSize);

        apiResultVo.setData(result);

        return apiResultVo; // 최종 API 응답 반환
    }

    @GetMapping("/api/dummy/picture")
    public ApiResultDto<Map<String, Object>> findAllPicture( //
            @RequestParam(defaultValue = "1") Integer page, //
            @RequestParam(defaultValue = "20") Integer pageSize //
    ) {
        ApiResultDto<Map<String, Object>> apiResultVo = new ApiResultDto<>(); // API 응답 객체 생성

        Map<String, Object> result = dummyService.findAllPicture(page, pageSize);

        apiResultVo.setData(result);

        return apiResultVo; // 최종 API 응답 반환
    }

}
