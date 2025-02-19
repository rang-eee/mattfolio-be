package com.colon.mattfolio.model.common;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value = "총 데이터 건수")
    private Integer totalElements;

    @ApiModelProperty(value = "페이지당 개수")
    private Integer size;

    @ApiModelProperty(value = "현재 페이지 번호")
    private Integer number;

    @ApiModelProperty(value = "총 페이지 개수")
    private int totalPages;

    @ApiModelProperty(value = "목록 데이터(T)")
    private List<T> content;

}
