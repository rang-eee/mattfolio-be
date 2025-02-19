package com.colon.mattfolio.model;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "HistoryLogRegisterRequestDto")
public class HistoryLogRegisterRequestDto {

    @ApiModelProperty(value = "요청 정보", example = "requestInfo")
    private String requestInfo;

    @ApiModelProperty(value = "생성자", example = "system")
    private String createdBy;

    @ApiModelProperty(value = "생성일시", example = "system")
    private LocalDateTime createdAt;

}
