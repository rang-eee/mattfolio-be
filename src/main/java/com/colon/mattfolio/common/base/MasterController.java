package com.colon.mattfolio.common.base;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.colon.mattfolio.model.common.ResourceDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 모든 Controller의 부모 클래스 <br>
 * Controller에서 사용되는 각종 응답 형태에 따른 메소드를 구현한 클래스이다.
 */
public class MasterController {

    /**
     * 웹 요청에 대한 응답데이터를 직접 처리하는 메소드
     * 
     * 문자열 데이터를 응답으로 전송
     * 
     * @param httpServletResponse 웹 요청에 대한 응답을 전송하기 위한 HttpServletResponse 객체
     * @param data 전송할 데이터
     * @throws IOException 응답 객체로부터 출력 스트림 객체를 가져올 수 없는 경우 해당 예외 발생
     */
    protected void responseWrite(HttpServletResponse httpServletResponse, String data) throws IOException {
        httpServletResponse.setContentType("text/html; charset=utf-8");
        httpServletResponse.setCharacterEncoding("UTF-8");

        Writer writer = null;

        try {
            // 데이터를 응답으로 전송
            writer = httpServletResponse.getWriter();
            writer.write(data);
        } finally {
            // 스트림을 닫아 리소스 누수 방지
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * 웹 요청에 대한 응답데이터를 직접 처리하는 메소드
     * 
     * 바이트 단위의 이진 데이터를 응답으로 전송 (주로 파일 다운로드 시 사용)
     * 
     * @param httpServletResponse 웹 요청에 대한 응답을 전송하기 위한 HttpServletResponse 객체
     * @param fileName 다운로드될 파일 이름
     * @param fileData 전송할 파일 데이터
     * @throws IOException 응답 객체로부터 출력 스트림 객체를 가져올 수 없는 경우 해당 예외 발생
     */
    protected void responseWrite(HttpServletResponse httpServletResponse, String fileName, byte[] fileData) throws IOException {
        // 파일 다운로드를 위한 응답 헤더 설정
        httpServletResponse.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", new String(fileName.getBytes(), "ISO-8859-1")));
        httpServletResponse.setContentType("text/html; charset=utf-8");
        httpServletResponse.setCharacterEncoding("UTF-8");

        OutputStream outStream = null;

        try {
            // 파일 데이터를 응답으로 전송
            outStream = httpServletResponse.getOutputStream();
            outStream.write(fileData, 0, fileData.length);
        } finally {
            // 스트림을 닫아 리소스 누수 방지
            if (outStream != null) {
                outStream.close();
            }
        }
    }

    /**
     * 파일 리소스를 응답으로 전송하는 메소드
     * 
     * 주로 파일 다운로드 기능에 사용된다.
     * 
     * @param attachResourceVo 다운로드할 파일 리소스 정보가 담긴 ResourceVo 객체
     * @return 파일 다운로드를 위한 ResponseEntity<Resource> 객체
     * @throws IOException 리소스를 읽을 수 없는 경우 발생하는 예외
     */
    protected ResponseEntity<Resource> responseFile(ResourceDto attachResourceVo) throws IOException {
        if (attachResourceVo == null) {
            return null; // 파일이 없으면 null 반환
        }

        // 파일 다운로드 응답 설정
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                .filename(attachResourceVo.getDownloadFileName(), StandardCharsets.UTF_8)
                .build()
                .toString())
            .body(attachResourceVo.getResource());
    }

    /**
     * 현재 요청된 URL의 쿼리스트링을 가져오는 메소드
     * 
     * @return URL에서 가져온 쿼리스트링을 반환
     */
    protected String getQueryString() {
        // 현재 요청 객체 가져오기
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String queryString = request.getQueryString(); // 요청된 쿼리스트링을 가져옴

        return queryString;
    }

    /**
     * 현재 요청된 URL의 쿼리스트링에서 특정 파라미터를 제거한 후 반환하는 메소드
     * 
     * @param removeParameterNames 쿼리스트링에서 제거할 파라미터의 이름, NULL인 경우 파라미터 제거 없이 쿼리스트링을 반환한다.
     * @return URL에서 가져온 쿼리스트링을 반환
     */
    protected String getQueryString(String[] removeParameterNames) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String queryString = null;

        // 제거할 파라미터가 없으면 그대로 쿼리스트링 반환
        if (removeParameterNames == null || removeParameterNames.length <= 0) {
            queryString = request.getQueryString();
        } else {
            // 제거할 파라미터가 있는 경우, 해당 파라미터를 쿼리스트링에서 제거
            queryString = this.removeQueryParameter(request.getQueryString(), removeParameterNames);
        }

        return queryString;
    }

    /**
     * URL의 쿼리스트링에서 특정 파라미터를 제거하는 메소드
     * 
     * @param url URL 문자열
     * @param removeParameterNames 쿼리스트링에서 제거할 파라미터명
     * @return 해당 파라미터명이 제거된 쿼리스트링 문자열 반환
     */
    protected String removeQueryParameter(String url, String[] removeParameterNames) {
        String tUrl = url;
        String result = null;

        try {
            // URL이 null이 아니고 제거할 파라미터가 있을 때 처리
            if (tUrl != null && removeParameterNames != null && removeParameterNames.length > 0) {
                // URL 문자열이 '?'로 시작하지 않으면 '?' 추가
                if (tUrl.indexOf("?") != 0) {
                    tUrl = "?" + tUrl;
                }

                // URIBuilder를 사용하여 쿼리 파라미터 제거
                URIBuilder uriBuilder = new URIBuilder(tUrl);
                List<NameValuePair> queryParameters = uriBuilder.getQueryParams();
                for (Iterator<NameValuePair> queryParameterItr = queryParameters.iterator(); queryParameterItr.hasNext();) {
                    NameValuePair queryParameter = queryParameterItr.next();

                    // 제거할 파라미터와 일치하는 항목을 찾아 리스트에서 제거
                    for (String removeParameterName : removeParameterNames) {
                        if (queryParameter.getName()
                            .equalsIgnoreCase(removeParameterName)) {
                            queryParameterItr.remove();
                        }
                    }
                }
                uriBuilder.setParameters(queryParameters);
                result = uriBuilder.build()
                    .toString();

                // 결과 문자열이 '?'로 시작하지 않는 경우 '?'를 추가
                if (result != null && result.indexOf("?") != 0) {
                    result = "?" + result;
                }
            }
        } catch (URISyntaxException e) {
            result = null; // URI 구문 오류 발생 시 null 반환
        }

        return result;
    }
}
