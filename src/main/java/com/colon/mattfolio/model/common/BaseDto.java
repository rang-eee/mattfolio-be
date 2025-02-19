package com.colon.mattfolio.model.common;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 기본 컬럼 DTO 클래스
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseDto {

    @ApiModelProperty(value = "사용 여부", example = "Y")
    private String useYn;

    @ApiModelProperty(value = "생성 ID", example = "1001")
    private Long createdId;

    @ApiModelProperty(value = "생성 일시", example = "2024-11-11T17:04:56.082147")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "수정 ID", example = "2002")
    private Long updatedId;

    @ApiModelProperty(value = "수정 일시", example = "2024-11-11T17:04:56.082147")
    private LocalDateTime updatedAt;
}
