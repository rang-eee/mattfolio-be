package com.colon.mattfolio.api.account.dto;

import com.colon.mattfolio.database.account.entity.AddressEntity;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AddressDto(@NotBlank String roadAddress,

        @NotBlank String addressDetail,

        @NotBlank String zipcode) {

    public static AddressDto fromEntity(AddressEntity address) {
        return AddressDto.builder()
            .roadAddress(address.getRoadAddress())
            .addressDetail(address.getAddressDetail())
            .zipcode(address.getZipcode())
            .build();
    }

    public AddressEntity toEntity() {
        return AddressEntity.builder()
            .roadAddress(roadAddress)
            .addressDetail(addressDetail)
            .zipcode(zipcode)
            .build();
    }
}
