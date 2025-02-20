package com.colon.mattfolio.model;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistoryLogRegisterRequestDto {

    @Schema(description = "요청 정보", example = "requestInfo")
    private String requestInfo;

    @Schema(description = "생성자", example = "system")
    private String createdBy;

    @Schema(description = "생성일시", example = "system")
    private LocalDateTime createdAt;

}
