package com.colon.mattfolio.model.common;

import org.springframework.core.io.Resource;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 파일 리소스와 파일 다운로드 이름을 포함하는 Vo 클래스
 * 
 * 파일 다운로드 응답에 필요한 파일 리소스와 파일명을 설정하기 위한 데이터 객체입니다.
 */
@Getter
@Setter
@Builder
public class ResourceDto {

    // 실제 파일 리소스를 담는 필드 (Spring의 Resource 객체)
    private Resource resource;

    // 다운로드 시 사용자에게 보여줄 파일 이름
    private String downloadFileName;
}
