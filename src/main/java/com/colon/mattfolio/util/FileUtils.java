package com.colon.mattfolio.util;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

/**
 * 파일 관련 유틸리티 클래스
 * 
 * 이 클래스는 파일의 MediaType을 결정하거나 미리보기가 가능한지 확인하는 기능을 제공합니다.<br/>
 * 파일 확장자를 기반으로 적절한 MediaType을 반환하며, 미리보기가 가능한 파일인지 확인하는 메서드를 포함합니다.
 * 
 * 주요 기능: <br/>
 * - 파일 확장자를 기반으로 MediaType 결정<br/>
 * - 특정 MediaType이 미리보기 가능한지 확인
 */
public class FileUtils {

    /**
     * 파일 확장자를 확인하여 적절한 MediaType을 반환하는 메서드
     * 
     * 파일 경로에서 확장자를 추출하고, 해당 확장자에 맞는 MediaType을 반환합니다.<br/>
     * 미리 정의되지 않은 확장자의 경우 기본값으로 APPLICATION_OCTET_STREAM을 반환합니다.
     * 
     * @param filePath MediaType을 결정할 파일 경로
     * @return 파일 확장자에 해당하는 MediaType 객체
     */
    public static MediaType determineMediaType(String filePath) {
        String extension = StringUtils.getFilenameExtension(filePath)
            .toLowerCase();

        switch (extension) {
        case "jpg":
        case "jpeg":
            return MediaType.IMAGE_JPEG;
        case "png":
            return MediaType.IMAGE_PNG;
        case "gif":
            return MediaType.IMAGE_GIF;
        case "pdf":
            return MediaType.APPLICATION_PDF;
        // 동영상 타입
        case "mpeg":
        case "mpg":
            return MediaType.valueOf("video/mpeg");
        case "mp4":
            return MediaType.valueOf("video/mp4");
        case "mov":
            return MediaType.valueOf("video/quicktime");
        case "avi":
            return MediaType.valueOf("video/x-msvideo");
        case "wmv":
            return MediaType.valueOf("video/x-ms-wmv");
        case "hevc":
            return MediaType.valueOf("video/hevc");
        // 오디오 타입
        case "mp3":
            return MediaType.valueOf("audio/mpeg");
        case "wav":
            return MediaType.valueOf("audio/wav");
        case "ogg":
            return MediaType.valueOf("audio/ogg");
        case "m4a":
            return MediaType.valueOf("audio/mp4");
        default:
            return MediaType.APPLICATION_OCTET_STREAM; // 기본값
        }
    }

    /**
     * 미리보기가 가능한 MediaType인지 확인하는 메서드
     * 
     * 입력된 MediaType이 이미지, 동영상, 또는 오디오 파일 유형인지 확인합니다. <br/>
     * 미리보기 가능한 유형이면 true, 그렇지 않으면 false를 반환합니다.
     * 
     * @param mediaType 확인할 MediaType 객체
     * @return 미리보기가 가능하면 true, 불가능하면 false
     */
    public static boolean isPreviewable(MediaType mediaType) {
        return mediaType.equals(MediaType.IMAGE_JPEG) //
                || mediaType.equals(MediaType.IMAGE_PNG) //
                || mediaType.equals(MediaType.IMAGE_GIF) //
                || mediaType.equals(MediaType.APPLICATION_PDF) //
                // 동영상 타입
                || mediaType.equals(MediaType.valueOf("video/mpeg")) //
                || mediaType.equals(MediaType.valueOf("video/mp4")) //
                || mediaType.equals(MediaType.valueOf("video/quicktime")) //
                || mediaType.equals(MediaType.valueOf("video/x-msvideo")) //
                || mediaType.equals(MediaType.valueOf("video/x-ms-wmv")) //
                || mediaType.equals(MediaType.valueOf("video/hevc")) //
                // 오디오 타입
                || mediaType.equals(MediaType.valueOf("audio/mpeg")) //
                || mediaType.equals(MediaType.valueOf("audio/wav")) //
                || mediaType.equals(MediaType.valueOf("audio/ogg")) //
                || mediaType.equals(MediaType.valueOf("audio/mp4")); //
    }
}