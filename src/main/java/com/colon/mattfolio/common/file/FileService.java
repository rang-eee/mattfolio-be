package com.colon.mattfolio.common.file;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.colon.mattfolio.common.exception.PhotoException;
import com.colon.mattfolio.external.blobStorage.service.BlobStorageService;
import com.colon.mattfolio.external.faceApi.dto.FaceDetectionResponse;
import com.colon.mattfolio.external.faceApi.dto.GroupResponse;
import com.colon.mattfolio.external.faceApi.service.FaceApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * FileService 클래스는 Azure Face API와 Azure Blob Storage를 이용하여 업로드된 이미지 파일을 처리하는 역할을 수행합니다.
 * 
 * 주요 기능은 다음과 같습니다:<br/>
 * - 업로드된 이미지 파일로부터 얼굴을 감지하고, 해당 얼굴의 ID를 추출<br/>
 * - 감지된 얼굴 ID들을 그룹화하여 동일 인물 매칭 등의 처리를 수행<br/>
 * - 처리된 이미지를 Azure Blob Storage에 업로드하고, 업로드된 파일의 URL을 반환<br/>
 * 
 * 이 클래스는 동기 방식으로 이미지 파일 처리 작업을 수행합니다.<br/>
 * 
 * 사용 예:<br/>
 * MultipartFile file = ...; // 클라이언트에서 업로드된 파일<br/>
 * fileService.processFile(file);
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class FileService {

    // Azure Face API를 호출하여 얼굴 감지 및 그룹화를 수행하는 서비스
    private final FaceApiService faceApiService;

    // Azure Blob Storage에 파일을 업로드하는 서비스를 담당
    private final BlobStorageService blobStorageService;

    /**
     * 업로드된 이미지 파일을 처리하는 메서드입니다.
     * 
     * 이 메서드는 다음 단계를 수행합니다:<br/>
     * 1. 파일 유효성 검증: 파일이 null이거나 비어있는 경우 예외 발생<br/>
     * 2. 파일을 바이트 배열로 읽어 Azure Face API를 통해 얼굴 감지 수행<br/>
     * 3. 감지된 얼굴 목록에서 얼굴 ID를 추출<br/>
     * 4. 추출된 얼굴 ID들을 그룹화하여 동일 인물 매칭 결과 확인<br/>
     * 5. 그룹화된 결과가 존재하면 Azure Blob Storage에 파일 업로드 후 URL 획득<br/>
     * 
     * @param file 클라이언트에서 업로드한 이미지 파일
     * @throws IOException 파일 입출력 중 발생하는 예외
     * @throws PhotoException 파일이 null이거나 비어있는 경우 발생하는 예외
     */
    public String processFile(MultipartFile file) throws IOException {
        // 파일 유효성 검사: 파일이 null이거나 비어있으면 예외 발생
        if (file == null || file.isEmpty()) {
            throw new PhotoException(PhotoException.Reason.EMPTY_FILES);
        }

        log.info("▶︎▶︎▶︎▶︎▶︎▶︎▶︎▶︎▶︎▶︎ 처리 시작 - 파일: {}", file.getOriginalFilename());

        // 파일을 바이트 배열로 변환 (Azure Face API 호출에 사용)
        byte[] imageBytes = file.getBytes();

        // Azure Face API를 사용하여 이미지 내 얼굴 감지 수행
        List<FaceDetectionResponse> faceDetections = faceApiService.detectFaces(imageBytes);
        if (faceDetections == null || faceDetections.isEmpty()) {
            // 얼굴이 감지되지 않은 경우 로그 출력 후 메서드 종료
            log.info("▶︎▶︎▶︎▶︎▶︎▶︎▶︎▶︎▶︎▶︎ Face Api 얼굴이 감지되지 않았습니다: {}", file.getOriginalFilename());
            return null;
        }

        // 감지된 얼굴 목록에서 각 얼굴의 고유 Face ID를 추출
        List<String> faceIds = faceDetections.stream()
            .map(FaceDetectionResponse::getFaceId)
            .collect(Collectors.toList());

        // **azure faceId 반환 관련 승인을 받은 후 사용 가능
        // // 추출한 Face ID를 기반으로 Azure Face API의 그룹화 기능 호출하여 동일 인물 매칭 수행
        // GroupResponse groupResponse = faceApiService.groupFaces(faceIds);
        // if (groupResponse != null) {
        // log.info("▶︎▶︎▶︎▶︎▶︎▶︎▶︎▶︎▶︎▶︎ Face Api 그룹화 결과 ({}): {}", file.getOriginalFilename(), groupResponse.toString());
        // } else {
        // // 그룹화 결과가 없는 경우 로그 출력 후 메서드 종료
        // log.info("▶︎▶︎▶︎▶︎▶︎▶︎▶︎▶︎▶︎▶︎ Face Ids가 존재하지 않습니다: {}", file.getOriginalFilename());
        // return null;
        // }

        // Azure Blob Storage에 파일 업로드 및 업로드된 파일 URL 획득
        // BlobStorageService.uploadImage 메서드의 첫번째 매개변수는 추가적인 옵션(예: 폴더 경로)로 사용될 수 있음
        String fileUrl = blobStorageService.uploadImage("", file);
        log.info("▶︎▶︎▶︎▶︎▶︎▶︎▶︎▶︎▶︎▶︎ Blob URL: {}", fileUrl);

        return fileUrl;
    }
}
