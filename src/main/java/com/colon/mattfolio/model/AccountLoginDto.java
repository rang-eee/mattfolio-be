package com.colon.mattfolio.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@ApiModel("AccountDto Login")
public class AccountLoginDto {

    @ApiModelProperty("사용자 아이디")
    private String username;

    @ApiModelProperty("사용자 password")
    private String password;

}
