package com.colon.mattfolio.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class AccountRequestDto {

    @Schema(description = "계정 ID")
    private Integer accountId;

    @Schema(description = "법인 코드")
    private String companyCode;

    @Schema(description = "사용자 ID")
    private String userId;

    @Schema(description = "계정 명")
    private String accountName;

    @Schema(description = "계정 이메일")
    private String accountEmail;

    @Schema(description = "계정 상태 코드")
    private String accountStatusCode;

    @Schema(description = "사용 여부")
    private String useYn;

}
