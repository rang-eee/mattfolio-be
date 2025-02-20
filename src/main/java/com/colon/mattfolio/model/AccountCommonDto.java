package com.colon.mattfolio.model;

import java.io.Serializable;

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
public class AccountCommonDto implements Serializable {

    @Schema(description = "계정 ID", example = "26368")
    private Long accountId;

    @Schema(description = "사용자 ID", example = "ohome90")
    private String userId;

    @Schema(description = "법인 코드", example = "OH0")
    private String companyCode;

    @Schema(description = "법인 내부 고유 번호", example = "1000_8703712")
    private String companyInternalUniqueNumber;

    @Schema(description = "계정 명", example = "홍길동")
    private String accountName;

    @Schema(description = "계정 이메일", example = "hong@gmail.com")
    private String accountEmail;

    @Schema(description = "계정 상태 코드", example = "")
    private String accountStatusCode;

    @Schema(description = "사용 여부", example = "Y")
    private String useYn;

}
