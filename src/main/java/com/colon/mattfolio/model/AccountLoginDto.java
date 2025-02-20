package com.colon.mattfolio.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AccountLoginDto {

    @Schema(description = "사용자 아이디")
    private String username;

    @Schema(description = "사용자 password")
    private String password;

}
