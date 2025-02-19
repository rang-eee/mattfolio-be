package com.colon.mattfolio.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@ApiModel("AccountDto Request")
public class AccountRequestDto {

    @ApiModelProperty("계정 ID")
    private Integer accountId;

    @ApiModelProperty("법인 코드")
    private String companyCode;

    @ApiModelProperty("사용자 ID")
    private String userId;

    @ApiModelProperty("계정 명")
    private String accountName;

    @ApiModelProperty("계정 이메일")
    private String accountEmail;

    @ApiModelProperty("계정 상태 코드")
    private String accountStatusCode;

    @ApiModelProperty("사용 여부")
    private String useYn;

}
