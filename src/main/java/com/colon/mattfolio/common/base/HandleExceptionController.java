package com.colon.mattfolio.common.base;

import java.lang.reflect.UndeclaredThrowableException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.colon.mattfolio.common.exception.MasterException;
import com.colon.mattfolio.common.property.Message;
import com.colon.mattfolio.model.common.ApiResultDto;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 예외 발생 시 핸들링을 위한 Controller 클래스
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class HandleExceptionController {

    // private final HistoryLogService historyLogService;

    /**
     * 파일업로드 용량 초과 오류 handler
     * 
     * 에러코드 994
     * 
     * @return 413 응답 데이터
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Object handleSizeLimitExceededException(HttpServletRequest request, MaxUploadSizeExceededException exception) {
        String logContent = getRequestInfo(request, exception);
        log.error(logContent);

        ApiResultDto<Void> result = new ApiResultDto<>();
        result.setResultCode(994);
        result.setRawResultMessage(Message.getMessage("common.proc.failed.uploadSize"));

        return new ResponseEntity<>(result, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    /**
     * 로그인 인증이 필요한 API를 비로그인 상태로 호출했을 경우에 대한 공통 로직
     * 
     * * 에러코드 995
     * 
     * @param exception
     * @return
     */
    @ExceptionHandler(value = { AuthenticationException.class, AccessDeniedException.class, org.springframework.security.access.AccessDeniedException.class })
    public ResponseEntity<ApiResultDto<Void>> handleAuthorityException(HttpServletRequest request, HttpServletResponse response, Exception exception) {

        // // 로그인 관련 갱신 토큰 쿠키 삭제
        // Cookie jrTokenCookie = new Cookie("jr_token", "");
        // jrTokenCookie.setMaxAge(0);
        // // jrTokenCookie.setDomain(baseDomain);
        // jrTokenCookie.setPath("/");
        // response.addCookie(jrTokenCookie);

        // // 로그인 관련 인증 토큰 쿠키 삭제
        // Cookie jTokenCookie = new Cookie("j_token", "");
        // jTokenCookie.setMaxAge(0);
        // // jTokenCookie.setDomain(baseDomain);
        // jTokenCookie.setPath("/");
        // response.addCookie(jTokenCookie);

        ApiResultDto<Void> result = new ApiResultDto<>();
        result.setResultCode(995);
        result.setResultMessage("common.login.required");

        String logContent = getRequestInfo(request, exception);
        log.error(logContent);

        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 404 에러에 대한 공통 로직
     * 
     * 에러코드 996
     * 
     * @return 404 응답 데이터
     */
    @ExceptionHandler(value = { NoHandlerFoundException.class, MissingServletRequestParameterException.class })
    public ResponseEntity<ApiResultDto<Void>> handleNoHandlerFoundException(HttpServletRequest request, Exception exception) {

        ApiResultDto<Void> result = new ApiResultDto<>();
        result.setResultCode(996);
        result.setResultMessage("common.proc.failed.invalidUrl");

        String logContent = getRequestInfo(request, exception);
        log.error(logContent);

        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }

    /**
     * 405 에러에 대한 공통 로직
     * 
     * 에러코드 997
     * 
     * @return 405 응답 데이터
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResultDto<Void>> handleHttpRequestMethodNotSupportedException(HttpServletRequest request, HttpRequestMethodNotSupportedException exception) {

        ApiResultDto<Void> result = new ApiResultDto<>();
        result.setResultCode(997);
        result.setResultMessage("common.proc.failed.invalidMethod");

        String logContent = getRequestInfo(request, exception);
        log.error(logContent);

        return new ResponseEntity<>(result, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * 입력 값에 대한 유효성 검증을 통과하지 못한 케이스를 위한 공통 로직
     * 
     * 에러코드 998
     * 
     * @param exception 예외 객체
     * @return 호출 실패에 대한 응답 데이터
     */
    @ExceptionHandler(value = { MethodArgumentNotValidException.class, ConstraintViolationException.class, MethodArgumentTypeMismatchException.class })
    public ResponseEntity<ApiResultDto<Void>> handleMethodArgumentNotValidException(HttpServletRequest request, Exception exception) {

        ApiResultDto<Void> result = new ApiResultDto<>();
        result.setResultCode(998);
        result.setRawResultMessage(exception.getMessage());

        if (exception instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) exception;

            List<ObjectError> allErrors = methodArgumentNotValidException.getBindingResult()
                .getAllErrors();

            if (allErrors != null && allErrors.isEmpty() == false) {
                result.setResultMessage(allErrors.get(0)
                    .getDefaultMessage());
            }

            // } else if (exception instanceof ConstraintViolationException constraintViolationException) {
        } else if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) exception;

            Set<ConstraintViolation<?>> constraintViolations = constraintViolationException.getConstraintViolations();

            if (constraintViolations.isEmpty() == false) {

                @SuppressWarnings("rawtypes")
                ConstraintViolation constraintViolation = constraintViolations.iterator()
                    .next();

                result.setResultMessage(constraintViolation.getMessageTemplate());
            }
        } else if (exception instanceof MethodArgumentTypeMismatchException) {
            MethodArgumentTypeMismatchException methodArgumentTypeMismatchException = (MethodArgumentTypeMismatchException) exception;

            result.setResultMessage("MethodArgumentTypeMismatchException: " + methodArgumentTypeMismatchException.getMessage());
        }

        String logContent = getRequestInfo(request, exception);
        log.error(logContent);

        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    /**
     * 커스텀 익셉션에 대한 핸들러
     * 
     * * 각 익셉션 코드
     * 
     * @param exception
     * @return
     */
    @ExceptionHandler(value = { MasterException.class })
    public ResponseEntity<ApiResultDto<Void>> handleCustomException(HttpServletRequest request, MasterException exception) {

        ApiResultDto<Void> result = new ApiResultDto<>();
        result.setResultCode(exception.getReasonCode());
        result.setResultMessage(exception.getReasonMessage());

        String logContent = getRequestInfo(request, exception);
        log.error(logContent);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 처리되지 않은 예외 발생 시 대응을 위한 공통 로직
     * 
     * 에러코드 999
     * 
     * @param request 요청 객체
     * @param exception 예외 객체
     * @return 호출 실패에 따른 응답 데이터
     */
    @ExceptionHandler
    public ResponseEntity<ApiResultDto<Void>> handleException(HttpServletRequest request, Exception exception) {
        exception.printStackTrace();

        String logContent = getRequestInfo(request, exception);
        log.error(logContent);

        // 로그 테이블 데이터 적재
        // @TODO 히스토리 적재
        // historyLogService.insertErrorLog(HistoryLogRegisterRequestDto.builder()
        // .requestInfo(logContent)
        // .createdBy("system")
        // .createdAt(LocalDateTime.now())
        // .build());

        return new ResponseEntity<>(new ApiResultDto<>(999, "common.proc.failed", null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 요청 정보를 문자열로 변환하여 반환 (공통 메서드)
     */
    private String getRequestInfo(HttpServletRequest request, Exception exception) {
        Throwable throwable = exception instanceof UndeclaredThrowableException ? exception.getCause() : exception;

        StringBuilder requestInfo = new StringBuilder();
        requestInfo.append(throwable.getMessage())
            .append("\n");
        requestInfo.append("URL : ")
            .append(request.getRequestURL())
            .append("\n");
        requestInfo.append("Method : ")
            .append(request.getMethod())
            .append("\n");

        requestInfo.append("Request header : \n");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            requestInfo.append("\t")
                .append(name)
                .append("=")
                .append(value)
                .append("\n");
        }

        // 본문 읽기
        String requestBody = getRequestBody(request);
        requestInfo.append("Request Body : \n")
            .append("\t")
            .append(requestBody)
            .append("\n");

        requestInfo.append("Parameter : \n");
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            String value = request.getParameter(name);
            String[] values = request.getParameterValues(name);
            if (values != null && values.length > 0) {
                for (String val : values) {
                    requestInfo.append("\t")
                        .append(name)
                        .append("=")
                        .append(val)
                        .append("\n");
                }
            } else {
                requestInfo.append("\t")
                    .append(name)
                    .append("=")
                    .append(value)
                    .append("\n");
            }
        }

        // 기타 정보 추가
        requestInfo.append("Cookie : \n");
        Cookie[] cookies = request.getCookies();
        for (int i = 0; cookies != null && i < cookies.length; i++) {
            requestInfo.append("\t")
                .append(cookies[i].getName())
                .append("=")
                .append(cookies[i].getValue())
                .append("\n");
        }

        return requestInfo.toString();
    }
    // private String getRequestInfo(HttpServletRequest request, Exception exception) {
    // Throwable throwable = null;

    // if (exception instanceof UndeclaredThrowableException == true) {
    // throwable = exception.getCause();
    // } else {
    // throwable = exception;
    // }

    // StringBuffer requestInfo = new StringBuffer();
    // requestInfo.append(throwable.getMessage() + "\n");
    // requestInfo.append("URL : " + request.getRequestURL()
    // .toString() + "\n");
    // requestInfo.append("Method : " + request.getMethod() + "\n");

    // requestInfo.append("Request header : \n");
    // Enumeration<String> headerNames = request.getHeaderNames();
    // while (headerNames.hasMoreElements()) {
    // String name = headerNames.nextElement();
    // String value = request.getHeader(name);
    // requestInfo.append("\t" + name + "=" + value + "\n");
    // }

    // String contentType = request.getContentType();
    // if (contentType != null && contentType.isEmpty() == false) {
    // if (contentType.toLowerCase()
    // .contains("json")) {
    // requestInfo.append("Request Body : \n");
    // try {
    // BufferedReader reader = request.getReader();
    // String tempLine = null;
    // while ((tempLine = reader.readLine()) != null) {
    // tempLine = tempLine.replace("\"", "");
    // requestInfo.append("\t" + tempLine + "\n");
    // }
    // } catch (IOException e) {
    // requestInfo.append("\tCannot get request body. (Message: " + e.getMessage() + ")\n");
    // }
    // }
    // }

    // requestInfo.append("Parameter : \n");
    // Enumeration<String> parameterNames = request.getParameterNames();
    // while (parameterNames.hasMoreElements()) {
    // String name = parameterNames.nextElement();
    // String value = request.getParameter(name);
    // String[] values = request.getParameterValues("name");

    // // value = maskingParameter(name, value);
    // // values = maskingParameter(name, values);

    // if (values != null && values.length > 0) {
    // for (int i = 0; i < values.length; i++) {
    // requestInfo.append("\t" + name + "=" + values[i] + "\n");
    // }
    // } else {
    // requestInfo.append("\t" + name + "=" + value + "\n");
    // }
    // }

    // requestInfo.append("Cookie : \n");
    // Cookie[] cookies = request.getCookies();
    // for (int i = 0; cookies != null && i < cookies.length; i++) {
    // String name = cookies[i].getName();
    // String value = cookies[i].getValue();
    // requestInfo.append("\t" + name + "=" + value + "\n");
    // }

    // requestInfo.append("Session : \n");
    // Enumeration<String> sessionNames = request.getSession()
    // .getAttributeNames();
    // Gson gson = new Gson();
    // while (sessionNames.hasMoreElements()) {
    // String name = sessionNames.nextElement();
    // Object obj = request.getSession()
    // .getAttribute(name);
    // String value = gson.toJson(obj);
    // requestInfo.append("\t[" + obj.getClass() + "] " + name + "=" + value + "\n");
    // }

    // return requestInfo.toString();
    // }

    private String getRequestBody(HttpServletRequest request) {
        if (!(request instanceof ContentCachingRequestWrapper)) {
            // 요청이 래핑되지 않은 경우 래핑
            request = new ContentCachingRequestWrapper(request);
        }
        ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;

        // 본문 캐시 읽기
        String requestBody = new String(wrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
        return requestBody.replace("\"", ""); // 본문에서 따옴표 제거
    }

}