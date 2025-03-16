package com.colon.mattfolio.external.faceApi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FaceDetectionResponse {
    @JsonProperty("faceId")
    private String faceId;

    @JsonProperty("faceRectangle")
    private FaceRectangle faceRectangle;

    @JsonProperty("faceAttributes")
    private FaceAttributes faceAttributes;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class FaceRectangle {
    private int top;
    private int left;
    private int width;
    private int height;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class FaceAttributes {
    private double age;
    private String gender;
    private double smile;
    // 기타 속성이 필요하면 추가
}
