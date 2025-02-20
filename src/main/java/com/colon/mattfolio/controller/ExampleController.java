package com.colon.mattfolio.controller;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.colon.mattfolio.common.base.MasterController;
import com.colon.mattfolio.exception.ExampleException;
import com.colon.mattfolio.exception.ExcelException;
import com.colon.mattfolio.model.AccountPrincipalDto;
import com.colon.mattfolio.model.common.ApiResultDto;
import com.colon.mattfolio.model.common.PagingResponseDto;
import com.colon.mattfolio.model.common.ResourceDto;
import com.colon.mattfolio.model.example.ExampleDetailResponseDto;
import com.colon.mattfolio.model.example.ExampleDownloadRequestDto;
import com.colon.mattfolio.model.example.ExampleExcelUploadRequestDto;
import com.colon.mattfolio.model.example.ExampleListRequestDto;
import com.colon.mattfolio.model.example.ExampleListResponseDto;
import com.colon.mattfolio.model.example.ExampleModifyRequestDto;
import com.colon.mattfolio.model.example.ExampleRegisterRequestDto;
import com.colon.mattfolio.security.AccountPrincipal;
import com.colon.mattfolio.service.ExampleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "000. [공통] 예제 관련 처리")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/v1/api/example")
@Secured({ "ROLE_USER" })
@Slf4j
public class ExampleController extends MasterController {

	private final ExampleService exampleService;

	/**
	 * 전체 목록 페이징 조회 API
	 * 
	 * 기본적으로 페이지 데이터 예제 항목을 조회하며, 선택적으로 검색 키워드와 검색 유형을 통해 필터링할 수 있습니다.
	 *
	 * @param request 검색 조건을 포함하는 요청 객체
	 * @param user 로그인 사용자 정보
	 * @return ApiResultDto<PagingResponseDto<ExampleListResponseDto>> 예제 항목 목록을 포함하는 API 응답 객체
	 */
	@Operation(summary = "전체 목록 페이징 조회", description = "기본적으로 페이지 데이터 예제 항목을 조회하며, 선택적으로 검색 조건을 바탕으로 예제 항목을 조회합니다.")
	@GetMapping("/list-page")
	public ApiResultDto<PagingResponseDto<ExampleListResponseDto>> getExamplesByCriteriaPaging(ExampleListRequestDto request, @Parameter(hidden = true) @AccountPrincipal AccountPrincipalDto user) {
		ApiResultDto<PagingResponseDto<ExampleListResponseDto>> apiResultVo = new ApiResultDto<>();

		PagingResponseDto<ExampleListResponseDto> result;

		// 기본 성공 메시지 설정
		apiResultVo.setResultCode(200);
		apiResultVo.setResultMessage("common.proc.success.search");

		try {
			result = exampleService.findByCriteriaPaging(request);
			if (ObjectUtils.isEmpty(result)) {
				apiResultVo.setData(null);
				apiResultVo.setResultMessage("common.proc.failed.search.empty");
			} else {
				apiResultVo.setData(result);
			}
		} catch (ExampleException e) {
			apiResultVo.setResultCode(e.getReasonCode());
			apiResultVo.setResultMessage(e.getReasonMessage());
		}
		return apiResultVo;
	}

	/**
	 * 전체 목록 조회 API
	 * 
	 * 기본적으로 모든 예제 항목을 조회하며, 선택적으로 검색 키워드와 검색 유형을 통해 필터링할 수 있습니다.
	 *
	 * @param request 검색 조건을 포함하는 요청 객체
	 * @return ApiResultDto<List<ExampleDetailResponseDto>> 예제 항목 목록을 포함하는 API 응답 객체
	 */
	@Operation(summary = "전체 목록 조회", description = "기본적으로 모든 예제 항목을 조회하며, 선택적으로 검색 조건을 바탕으로 예제 항목을 조회합니다.")
	@GetMapping("/list")
	public ApiResultDto<List<ExampleDetailResponseDto>> getExamplesByCriteria(ExampleListRequestDto request) {

		ApiResultDto<List<ExampleDetailResponseDto>> apiResultVo = new ApiResultDto<>();
		List<ExampleDetailResponseDto> result;

		apiResultVo.setResultCode(200);
		apiResultVo.setResultMessage("common.proc.success.search");

		try {
			result = exampleService.findByCriteria(request);
			if (ObjectUtils.isEmpty(result)) {
				apiResultVo.setData(null);
				apiResultVo.setResultMessage("common.proc.failed.search.empty");
			} else {
				apiResultVo.setData(result);
			}
		} catch (ExampleException e) {
			apiResultVo.setResultCode(e.getReasonCode());
			apiResultVo.setResultMessage(e.getReasonMessage());
		}

		return apiResultVo;
	}

	/**
	 * 단일 항목 세부 조회 API
	 * 
	 * 특정 예제 항목을 ID로 조회합니다.
	 *
	 * @param userId 예제 항목의 ID (유저 아이디)
	 * @return ApiResultDto<ExampleDetailResponseDto> 예제 항목 정보를 포함하는 API 응답 객체
	 */
	@Operation(summary = "단일 항목 세부 조회", description = "특정 예제 항목을 ID로 조회합니다.")
	@GetMapping("/{userId}")
	public ApiResultDto<ExampleDetailResponseDto> getExampleById(@Parameter(description = "유저 아이디", example = "1") @PathVariable String userId) {

		ApiResultDto<ExampleDetailResponseDto> apiResultVo = new ApiResultDto<>();
		ExampleDetailResponseDto result;

		apiResultVo.setResultCode(200);
		apiResultVo.setResultMessage("common.proc.success.search");

		result = exampleService.findById(userId);
		if (ObjectUtils.isEmpty(result)) {
			apiResultVo.setData(null);
			apiResultVo.setResultMessage("common.proc.failed.search.empty");
		} else {
			apiResultVo.setData(result);
		}
		return apiResultVo;
	}

	/**
	 * 새로운 예제 항목 생성 API
	 * 
	 * 새로운 예제 항목을 생성합니다.
	 *
	 * @param request 생성할 예제 항목 정보를 담은 요청 객체
	 * @return ApiResultDto<ExampleDetailResponseDto> 생성된 예제 항목 정보를 포함하는 API 응답 객체
	 */
	@Operation(summary = "새로운 예제 항목 생성", description = "새로운 예제 항목을 생성합니다.")
	@PostMapping("")
	public ApiResultDto<ExampleDetailResponseDto> createExample(@RequestBody ExampleRegisterRequestDto request) {

		ApiResultDto<ExampleDetailResponseDto> apiResultVo = new ApiResultDto<>();
		apiResultVo.setResultCode(200);
		apiResultVo.setResultMessage("common.proc.success.register");

		try {
			ExampleDetailResponseDto createdExample = exampleService.create(request);
			apiResultVo.setData(createdExample);
		} catch (ExampleException e) {
			apiResultVo.setResultCode(e.getReasonCode());
			apiResultVo.setResultMessage(e.getReasonMessage());
		}

		return apiResultVo;
	}

	/**
	 * 기존 예제 항목 수정 API
	 * 
	 * 특정 ID의 예제 항목을 수정합니다.
	 *
	 * @param userId 예제 항목의 ID (유저 아이디)
	 * @param request 수정할 예제 항목 정보를 담은 요청 객체
	 * @return ApiResultDto<ExampleDetailResponseDto> 수정된 예제 항목 정보를 포함하는 API 응답 객체
	 */
	@Operation(summary = "기존 예제 항목 수정", description = "특정 ID의 예제 항목을 수정합니다.")
	@PutMapping("/{userId}")
	public ApiResultDto<ExampleDetailResponseDto> updateExample(@Parameter(description = "유저 아이디", example = "1") @PathVariable String userId, @RequestBody ExampleModifyRequestDto request) {

		ApiResultDto<ExampleDetailResponseDto> apiResultVo = new ApiResultDto<>();

		try {
			request.setUserId(userId);
			ExampleDetailResponseDto updatedExample = exampleService.update(request);
			apiResultVo.setResultCode(200);
			apiResultVo.setResultMessage("common.proc.success.update");
			apiResultVo.setData(updatedExample);
		} catch (ExampleException e) {
			apiResultVo.setResultCode(e.getReasonCode());
			apiResultVo.setResultMessage(e.getReasonMessage());
		}

		return apiResultVo;
	}

	/**
	 * 기존 예제 항목 삭제 API
	 * 
	 * 특정 ID의 예제 항목을 삭제합니다.
	 *
	 * @param userId 예제 항목의 ID (유저 아이디)
	 * @return ApiResultDto<Boolean> 삭제 성공 여부를 포함하는 API 응답 객체
	 */
	@Operation(summary = "기존 예제 항목 삭제", description = "특정 ID의 예제 항목을 삭제합니다.")
	@DeleteMapping("/{userId}")
	public ApiResultDto<Boolean> deleteExample(@Parameter(description = "유저 아이디", example = "1") @PathVariable String userId) {

		ApiResultDto<Boolean> apiResultVo = new ApiResultDto<>();

		try {
			boolean deleted = exampleService.delete(userId);
			apiResultVo.setResultCode(200);
			apiResultVo.setResultMessage("common.proc.success.delete");
			apiResultVo.setData(deleted);
		} catch (ExampleException e) {
			apiResultVo.setResultCode(e.getReasonCode());
			apiResultVo.setResultMessage(e.getReasonMessage());
		}
		return apiResultVo;
	}

	/**
	 * 엑셀 업로드 예제 API
	 * 
	 * 엑셀 파일을 업로드하여 데이터를 저장합니다.
	 *
	 * @param request 엑셀 업로드 요청 정보
	 * @param file 첨부 파일
	 */
	@Operation(summary = "엑셀 업로드 예제", description = "엑셀을 업로드하여 형식에 맞게 데이터를 저장합니다.")
	@PostMapping(value = "/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void uploadExcel(@ModelAttribute(value = "request") ExampleExcelUploadRequestDto request, @RequestPart(value = "file") @Parameter(description = "첨부파일") MultipartFile file) {
		try {
			exampleService.uploadExcel(request, file);
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * 정적 파일 다운로드 및 미리보기 예제 API
	 * 
	 * 정적 파일을 다운로드 또는 미리보기합니다.
	 *
	 * @param request Open API 다운로드 요청 정보
	 * @param user 로그인 사용자 정보
	 * @return ResponseEntity<Resource> 정적 파일 리소스
	 * @throws IOException 파일 처리 중 예외 발생 시
	 */
	@Operation(summary = "정적 파일 다운로드 및 미리보기 예제", description = "정적 파일 다운로드 또는 미리보기합니다.")
	@PostMapping("/exceldownload")
	public ResponseEntity<Resource> excelDownloadCompanyList(@Valid @RequestBody ExampleDownloadRequestDto request, @Parameter(hidden = true) @AccountPrincipal AccountPrincipalDto user) throws IOException {
		try {
			ResourceDto resource = exampleService.downloadExcelTemplate(request);
			if (resource == null)
				throw new ExcelException(ExcelException.Reason.INVALID_RESOURCE);
			return responseFile(resource);
		} catch (ExcelException e) {
			throw e;
		}
	}

	/**
	 * S3 이미지 미리보기 API
	 * 
	 * S3에 업로드된 이미지를 미리보기합니다.
	 *
	 * @param user 로그인 사용자 정보
	 * @param isPreview 미리보기 여부
	 * @return ResponseEntity<Resource> 이미지 리소스
	 * @throws IOException 파일 처리 중 예외 발생 시
	 */
	@Operation(summary = "S3 이미지 미리보기")
	@GetMapping("/preview")
	public ResponseEntity<Resource> preview(@Parameter(hidden = true) @AccountPrincipal AccountPrincipalDto user, @Parameter(description = "미리보기 여부", example = "true") Boolean isPreview) throws IOException {
		try {
			Resource resource = new UrlResource("https://d1oyf0frugj3qi.cloudfront.net/dev_tqms/2025/01/20973/source.jpg?null");
			return responseFile(ResourceDto.builder()
				.resource(resource)
				.downloadFileName("download")
				.mediaType(MediaType.IMAGE_JPEG)
				.preview(isPreview)
				.build());
		} catch (ExcelException e) {
			throw e;
		}
	}

}
