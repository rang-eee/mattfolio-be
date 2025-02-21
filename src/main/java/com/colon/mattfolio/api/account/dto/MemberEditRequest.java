package com.colon.mattfolio.api.account.dto;

import jakarta.validation.constraints.NotBlank;

public record MemberEditRequest( //
                @NotBlank String name, //
                AddressDto address //
) {
}
