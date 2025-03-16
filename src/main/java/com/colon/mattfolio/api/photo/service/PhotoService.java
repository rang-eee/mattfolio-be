package com.colon.mattfolio.api.photo.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.colon.mattfolio.common.exception.PhotoException;
import com.colon.mattfolio.common.file.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PhotoService는 업로드된 사진 파일에 대해 파일 처리 및 업로드 테스트 기능을 제공하는 서비스 클래스입니다.
 * 
 * 주요 기능:<br/>
 * - 업로드된 파일 배열을 동기적으로 처리하여 각 파일에 대해 FileService의 processFile 메서드를 호출<br/>
 * - 업로드 테스트를 통해 동기 처리 지연을 시뮬레이션함
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class PhotoService {

    private final FileService fileService;

    /**
     * 여러 파일을 동기적으로 처리하는 메서드입니다.<br/>
     * 파일 배열이 null이거나 비어있는 경우 예외를 발생시키며, 각 파일에 대해 FileService.processFile 메서드를 호출합니다.<br/>
     * 
     * @param files 클라이언트에서 업로드된 파일 배열
     * @throws IOException 파일 입출력 중 발생하는 예외
     * @throws PhotoException 파일 배열이 null이거나 비어있을 때 발생하는 예외
     */
    public void uploadFiles(MultipartFile[] files) throws IOException {
        if (files == null || files.length < 1) {
            throw new PhotoException(PhotoException.Reason.EMPTY_FILES);
        }

        // 배열 내의 각 파일을 순차적으로 처리합니다.
        for (MultipartFile file : files) {
            String fileUrl = fileService.processFile(file);

        }
    }

    /**
     * 업로드 테스트용 메서드입니다.<br/>
     * 동기적으로 5초간 지연을 시뮬레이션하여 업로드 테스트를 수행합니다.
     */
    public void uploadTest() {
        try {
            log.info("uploadTest 시작 - Thread.sleep 실행 전");
            Thread.sleep(5000); // 5초 지연
            log.info("uploadTest 종료 - Thread.sleep 완료");
        } catch (InterruptedException e) {
            Thread.currentThread()
                .interrupt();
        }
    }
}
