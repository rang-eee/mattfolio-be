package com.colon.mattfolio.api.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private String name;
    private String email;
    private String profile;

    // public static AccountDto fromEntity(AccountEntity member) {
    // AccountDto memberDto = AccountDto.builder()
    // .name(member.getName())
    // .email(member.getEmail())
    // .profile(member.getProfile())
    // .build();

    // if (member.getAddress() != null) {
    // memberDto.address = AddressDto.fromEntity(member.getAddress());
    // }
    // return memberDto;
    // }
}
