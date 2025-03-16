package com.colon.mattfolio.api.photo.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.colon.mattfolio.api.photo.service.PhotoService;
import com.colon.mattfolio.common.base.MasterController;
import com.colon.mattfolio.common.dto.ApiResultDto;
import com.colon.mattfolio.common.exception.PhotoException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PhotoController는 회원의 사진 파일 업로드와 업로드 테스트 기능을 제공하는 REST 컨트롤러입니다.<br/>
 * <br/>
 * 주요 기능:<br/>
 * - 업로드된 파일들을 동기적으로 처리하여 결과를 반환<br/>
 * - 업로드 테스트를 통해 동기 처리 지연을 시뮬레이션함
 */
@RestController("/api/photo")
@RequiredArgsConstructor
@Slf4j
public class PhotoController extends MasterController {

    private final PhotoService photoService;

    /**
     * 회원 사진 파일 업로드 API<br/>
     * 업로드된 파일들을 동기적으로 처리하여 결과가 완료된 후 응답을 반환합니다.<br/>
     * <br/>
     * 
     * @param files 업로드할 MultipartFile 배열 (필수)
     * @return ApiResultDto&lt;String&gt; - 업로드 결과에 대한 응답 객체
     * @throws PhotoException 파일 업로드 중 오류 발생 시 예외 발생
     */
    @Operation(summary = "회원 사진 파일 업로드", description = "회원 사진 파일들을 업로드합니다. 업로드 작업은 동기적으로 처리되어 결과가 완료된 후 응답을 반환합니다.", requestBody = @RequestBody(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)))
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResultDto<String> uploadFiles( //
            @Parameter(description = "업로드할 파일들", required = true) @RequestParam("files") MultipartFile[] files, //
            @AuthenticationPrincipal UserDetails user) {
        ApiResultDto<String> apiResultVo = new ApiResultDto<>();
        try {
            // 동기 방식으로 파일 업로드 및 처리를 수행합니다.
            photoService.uploadFiles(files);
        } catch (IOException e) {
            throw new PhotoException(PhotoException.Reason.INVALID_FILE);
        }
        return apiResultVo;
    }

    /**
     * 업로드 테스트 API<br/>
     * 동기 방식으로 5초 지연을 시뮬레이션하여 업로드 테스트를 수행합니다.<br/>
     * <br/>
     * 
     * @return ApiResultDto&lt;String&gt; - 테스트 완료 메시지를 포함한 응답 객체
     * @throws PhotoException 업로드 테스트 중 오류 발생 시 예외 발생
     */
    @Operation(summary = "업로드 테스트", description = "동기 방식으로 업로드 테스트를 진행합니다.")
    @PostMapping(value = "/uploadTest")
    public ApiResultDto<String> uploadTest() {
        ApiResultDto<String> apiResultVo = new ApiResultDto<>();
        try {
            // 동기적으로 업로드 테스트를 수행합니다.
            photoService.uploadTest();
            apiResultVo.setData("업로드 테스트가 완료되었습니다!");
        } catch (Exception e) {
            throw new PhotoException(PhotoException.Reason.INVALID_FILE);
        }
        return apiResultVo;
    }
}
