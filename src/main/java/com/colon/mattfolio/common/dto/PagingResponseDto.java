package com.colon.mattfolio.common.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 페이징 처리된 데이터를 Response 하기 위한 Vo 클래스
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PagingResponseDto<T> {

    @Schema(description = "총 데이터 건수")
    private Integer totalElements;

    @Schema(description = "페이지당 개수")
    private Integer size;

    @Schema(description = "현재 페이지 번호")
    private Integer number;

    @Schema(description = "총 페이지 개수")
    private int totalPages;

    @Schema(description = "목록 데이터(T)")
    private List<T> content;

}
