package com.colon.mattfolio.model.example;

import com.colon.mattfolio.model.common.PagingRequestDto;

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
public class ExampleListRequestDto extends PagingRequestDto {

    @Schema(description = "검색 키워드", example = "홍길동")
    private String searchKeyword;

    @Schema(description = "검색 타입", example = "userName")
    private String searchType;

}
