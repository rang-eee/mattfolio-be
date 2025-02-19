package com.colon.mattfolio.model.common;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 페이징 처리된 데이터를 Response 하기 위한 Vo 클래스
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PagingRequestDto {

    @Builder.Default
    @ApiModelProperty(value = "현재 페이지", required = false, position = 1, example = "1")
    private Integer page = 1;

    @Builder.Default
    @ApiModelProperty(value = "한 페이지당 표시 갯수", required = false, position = 2, example = "20")
    private Integer limit = 20;

    @Builder.Default
    @ApiModelProperty(value = "기본 정렬 조건", required = false, position = 3, example = "createdAt")
    private String sort = "createdAt";

    @Builder.Default
    @ApiModelProperty(value = "정렬 방향", required = false, position = 4, example = "desc")
    private String dir = "desc";

    @ApiModelProperty(value = "다중 정렬 조건", required = false, position = 5, example = "[{\"name\":\"createdAt\", \"dir\":\"desc\"}, {\"name\":\"updatedAt\", \"dir\":\"asc\"}]")
    private String sortsJsonString;

    @ApiModelProperty(value = "다중 정렬 조건", hidden = true) // 사용자에게 노출되지 않음
    private List<PagingRequestDto.Sort> sorts;

    @ApiModelProperty(value = "offset", hidden = true)
    private Integer offset = 0;

    @Builder.Default
    @ApiModelProperty(value = "searchAll", hidden = true)
    private Boolean searchAll = false; // 엑셀 다운로드등 limitg, offset 사용을 하지 않을 경우. 기본값 = false

    /**
     * offset은 page -1로 설정
     */
    public void setPage(Integer page) {
        this.page = page;
        this.offset = limit * ((page < 1) ? 0 : (page - 1));
    }

    /**
     * sorts가 null이거나 비어 있는 경우 기본값 반환 하지만 sort나 dir이 비어 있으면 null 반환
     */
    public List<PagingRequestDto.Sort> getSorts() {
        // sort 또는 dir이 null이거나 비어 있으면 null 반환
        if (this.sort == null || this.sort.isEmpty() || this.dir == null || this.dir.isEmpty()) {
            return List.of();
        }

        // sorts가 null이거나 비어 있으면 기본값 설정
        if (this.sorts == null || this.sorts.isEmpty()) {
            this.sorts = List.of(new PagingRequestDto.Sort(this.sort, this.dir));
        }
        return this.sorts;
    }

    /**
     * sortsJsonString을 설정할 때 sorts 리스트를 자동으로 업데이트
     */
    public void setSortsJsonString(String sortsJsonString) {
        this.sortsJsonString = sortsJsonString;

        if (sortsJsonString != null && !sortsJsonString.isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                this.sorts = objectMapper.readValue(sortsJsonString, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, PagingRequestDto.Sort.class));
            } catch (JsonProcessingException e) {
                // throw new IllegalArgumentException("Invalid JSON format for sortsJsonString", e);
                this.sorts = List.of(new PagingRequestDto.Sort(this.sort, this.dir));
            }
        } else {
            // 기본 정렬 조건 설정
            this.sorts = List.of(new PagingRequestDto.Sort(this.sort, this.dir));
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Sort {

        @ApiModelProperty(value = "정렬 필드명", hidden = true)
        private String name;

        @ApiModelProperty(value = "정렬 방향 (asc, desc)", hidden = true)
        private String dir;

        public void setDir(String dir) {
            this.dir = (dir != null) ? dir.toLowerCase() : null;
        }
    }
}
