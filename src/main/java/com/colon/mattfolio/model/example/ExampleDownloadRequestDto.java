package com.colon.mattfolio.model.example;

import com.colon.mattfolio.validate.Validation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ExampleDownloadRequestDto {

    @Schema(description = "파일 경로", example = "templates/excels/example_upload_template.xlsx", required = true)
    @Validation(field = "filePath", notEmpty = true)
    private String filePath;

    @Schema(description = "다운로드할 파일 명", example = "엑셀_양식_테스트")
    private String downloadName;

    @Schema(description = "미리보기 여부", example = "false")
    private Boolean preview;

}