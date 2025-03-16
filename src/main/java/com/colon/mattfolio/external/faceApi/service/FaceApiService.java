package com.colon.mattfolio.external.faceApi.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.colon.mattfolio.external.faceApi.dto.FaceDetectionResponse;
import com.colon.mattfolio.external.faceApi.dto.GroupResponse;

@Service
public class FaceApiService {

    @Value("${azure.face.endpoint}")
    private String faceEndpoint;

    @Value("${azure.face.subscription-key}")
    private String subscriptionKey;

    private final WebClient webClient;

    public FaceApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * 얼굴 감지 API 호출 (Detect)
     */
    public List<FaceDetectionResponse> detectFaces(byte[] imageBytes) {
        String url = faceEndpoint + "/face/v1.0/detect";
        // String url = faceEndpoint + "/face/v1.0/detect" //
        // + "?returnFaceId=false" //
        // + "&returnFaceLandmarks=false" //
        // + "&returnFaceAttributes=age,gender,smile"
        ;

        return webClient.post()
            .uri(url)
            .header("Ocp-Apim-Subscription-Key", subscriptionKey)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .bodyValue(imageBytes)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<FaceDetectionResponse>>() {
            })
            .block();
    }

    /**
     * 그룹화 API 호출 (Group)
     */
    public GroupResponse groupFaces(List<String> faceIds) {
        List<String> groupFaceIds = faceIds.stream()
            .filter(id -> id != null)
            .collect(Collectors.toList());

        if (groupFaceIds.isEmpty()) {
            return null;
        }

        String url = faceEndpoint + "/face/v1.0/group";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("faceIds", faceIds);

        return webClient.post()
            .uri(url)
            .header("Ocp-Apim-Subscription-Key", subscriptionKey)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(GroupResponse.class)
            .block();
    }
}
