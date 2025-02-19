package com.colon.mattfolio.model;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("AccountDto Common")
public class AccountCommonDto implements Serializable {

    @ApiModelProperty(value = "계정 ID", example = "26368")
    private Long accountId;

    @ApiModelProperty(value = "사용자 ID", example = "ohome90")
    private String userId;

    @ApiModelProperty(value = "법인 코드", example = "OH0")
    private String companyCode;

    @ApiModelProperty(value = "법인 내부 고유 번호", example = "1000_8703712")
    private String companyInternalUniqueNumber;

    @ApiModelProperty(value = "계정 명", example = "홍길동")
    private String accountName;

    @ApiModelProperty(value = "계정 이메일", example = "hong@gmail.com")
    private String accountEmail;

    @ApiModelProperty(value = "계정 상태 코드", example = "")
    private String accountStatusCode;

    @ApiModelProperty(value = "사용 여부", example = "Y")
    private String useYn;

}
