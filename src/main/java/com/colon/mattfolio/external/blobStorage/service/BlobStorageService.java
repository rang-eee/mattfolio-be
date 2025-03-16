package com.colon.mattfolio.external.blobStorage.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

@Service
public class BlobStorageService {

    @Value("${azure.blob.contection-string}")
    private String connectionString;

    @Value("${azure.blob.container.profile.name}")
    private String profileContainer;

    @Value("${azure.blob.container.profile.token}")
    private String profileContainerToken;

    @Value("${azure.blob.container.photographer.name}")
    private String photographerContainer;
    @Value("${azure.blob.container.photographer.token}")
    private String photographerContainerToken;

    /**
     * Blob Storage에 이미지 업로드
     *
     * @param file 업로드할 MultipartFile
     * @return 업로드된 파일의 URL
     * @throws IOException 파일 처리 중 발생할 수 있는 예외
     */
    public String uploadImage(String faceId, MultipartFile file) throws IOException {
        // BlobServiceClient 생성
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString)
            .buildClient();

        // Blob ContainerClient 획득
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(profileContainer);

        // 고유한 blob 이름 생성 (예: UUID + 원본 파일명)
        String blobName = generateUniqueBlobName(file.getOriginalFilename());
        String uploadName = faceId.isEmpty() ? "" + blobName : faceId + "_" + blobName;

        // BlobClient 생성
        BlobClient blobClient = containerClient.getBlobClient(uploadName);

        // 파일 업로드 (이미 동일한 이름이 있을 경우 덮어쓰기(true))
        blobClient.upload(file.getInputStream(), file.getSize(), true);

        // 업로드된 파일의 URL 반환 (이 URL을 DB에 저장하는 등 후속 처리가 가능)
        return blobClient.getBlobUrl();
    }

    /**
     * 파일명을 고유하게 생성하는 메서드
     */
    private String generateUniqueBlobName(String originalFilename) {
        String uuid = UUID.randomUUID()
            .toString();
        return uuid + "_" + originalFilename;
    }
}
