package com.colon.mattfolio.common.auth;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.colon.mattfolio.common.dto.ApiResultDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * SecurityAuthenticationFailEntryPoint는 인증 실패 시 실행되어 클라이언트에게 커스텀 에러 응답을 반환하는 역할을 합니다. 이 클래스는 AuthenticationEntryPoint 인터페이스를 구현하여, 인증되지 않은 사용자에 대해 JSON 형식의 에러 메시지를 반환합니다.
 */
public class SecurityAuthenticationFailEntryPoint implements AuthenticationEntryPoint {

    // ObjectMapper를 사용하여 객체를 JSON 문자열로 변환합니다.
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 인증 실패 시 호출되는 메서드입니다.
     *
     * @param request 현재 HTTP 요청
     * @param response 현재 HTTP 응답
     * @param authException 인증 예외 객체 (인증 실패 관련 정보 포함)
     * @throws IOException 입출력 예외 발생 시 던짐
     * @throws ServletException 서블릿 관련 예외 발생 시 던짐
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // ApiResultDto 객체를 생성하여 에러 응답 포맷을 구성합니다.
        ApiResultDto<Void> result = new ApiResultDto<>();
        // 에러 코드 설정 (-401은 인증 실패를 의미)
        result.setResultCode(-401);
        // 에러 메시지 설정 (클라이언트에게 보여줄 메시지)
        result.setResultMessage("인증이 필요합니다. 로그인 후 다시 시도하세요.");
        // 추가 데이터는 없으므로 null로 설정
        result.setData(null);

        // ObjectMapper를 사용해 result 객체를 JSON 문자열로 직렬화합니다.
        String json = objectMapper.writeValueAsString(result);

        // 응답의 Content-Type을 application/json으로 설정하여 JSON 데이터임을 명시합니다.
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + "; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        // 응답 상태 코드를 401 Unauthorized로 설정합니다.
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        // 응답 스트림을 얻어 JSON 문자열을 클라이언트에 출력합니다.
        PrintWriter writer = response.getWriter();
        writer.write(json);
        // 스트림을 flush하여 출력 버퍼의 내용을 전송합니다.
        writer.flush();
    }
}
