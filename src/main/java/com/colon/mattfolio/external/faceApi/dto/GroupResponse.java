package com.colon.mattfolio.external.faceApi.dto;

import java.util.List;

import lombok.Data;

@Data
public class GroupResponse {
    // 그룹별 얼굴 ID 리스트
    private List<List<String>> groups;

    // 분류되지 못한 얼굴 ID 리스트
    private List<String> messyGroup;
}
