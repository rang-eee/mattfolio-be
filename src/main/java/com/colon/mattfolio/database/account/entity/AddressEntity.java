package com.colon.mattfolio.database.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class AddressEntity {

    private String roadAddress;

    private String addressDetail;

    @Column(length = 10)
    private String zipcode;

    @Builder
    public AddressEntity(String roadAddress, String addressDetail, String zipcode) {
        this.roadAddress = roadAddress;
        this.addressDetail = addressDetail;
        this.zipcode = zipcode;
    }
}
