package com.colon.mattfolio.api.dummy.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class DummyService {

    public Map<String, Object> findAllFestival(Integer page, Integer pageSize) {
        Map<String, Object> result = new HashMap<>();

        // 전체 50개의 페스티벌 데이터를 생성
        List<Map<String, Object>> allFestivals = new ArrayList<>();
        Random random = new Random();
        // 예시로 사용할 이름 목록
        String[] names = { "Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace", "Heidi", "Ivan", "Judy" };
        String[] images = { //
                "https://www.apparelnews.co.kr/upfiles/manage/202010/fec559517a1bd71b6ffc3d4428401c07.jpg", //
                "https://static.nike.com/a/images/f_auto/dpr_3.0,cs_srgb/h_484,c_limit/c13d3272-66a8-4b73-bf90-b386b1bac95b/%EC%99%84%EB%B2%BD%ED%95%9C-%EB%9F%AC%EB%8B%9D%EC%9D%84-%EC%99%84%EC%84%B1%ED%95%98%EB%8A%94-%EB%B0%A9%EB%B2%95.jpg",
                "https://as1.ftcdn.net/jpg/02/51/13/00/1000_F_251130019_OH9dIOh3l34Pu780AHLkckmNiM2Lkcjz.jpg", //
                "https://marathonhandbook.com/wp-content/uploads/2022/10/Running-Coach-Certification.jpg" //
        };

        for (int i = 0; i < 50; i++) {
            Map<String, Object> festival = new HashMap<>();

            // 대회ID (자동 증가 값)
            festival.put("festivalId", "FST" + (i + 1));

            // 날짜: 2020년부터 2025년 사이의 랜덤 날짜 (월/일은 간단하게 1~28 사이 선택)
            int year = random.nextInt(6) + 2020; // 2020 ~ 2025
            int month = random.nextInt(12) + 1; // 1 ~ 12
            int day = random.nextInt(28) + 1; // 1 ~ 28
            String date = String.format("%04d-%02d-%02d", year, month, day);
            festival.put("date", date);

            // 대회명
            festival.put("festivalName", "Festival " + (i + 1));

            // 등록자명: 미리 정의된 이름 목록에서 랜덤 선택
            String registerName = names[random.nextInt(names.length)];
            festival.put("registerName", registerName);

            // 등록자 사진 URL: 예시 URL (미리 정의된 이미지 목록 중 랜덤 선택)
            String registerImage = images[random.nextInt(images.length)];
            festival.put("registerPhotoId", "PTO" + (i + 1));
            festival.put("registerPhotoUrl", registerImage);

            // 썸네일 URL: 예시 URL (미리 정의된 이미지 목록 중 랜덤 선택)
            String registerThumb = images[random.nextInt(images.length)];
            festival.put("thumbnailId", "TBN" + (i + 1));
            festival.put("thumbnailUrl", registerThumb);

            // 등록된 사진 개수: 1부터 100 사이의 랜덤 값
            festival.put("photoCount", random.nextInt(100) + 1);

            allFestivals.add(festival);
        }

        // 페이징 처리
        int total = allFestivals.size();
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);

        // startIndex가 전체 데이터 범위를 벗어나지 않는지 확인
        List<Map<String, Object>> pageList = new ArrayList<>();
        if (startIndex < total) {
            pageList = allFestivals.subList(startIndex, endIndex);
        }
        // 더보기 여부: 현재 페이지의 마지막 인덱스가 전체 데이터 개수보다 작은 경우
        boolean more = endIndex < total;

        result.put("list", pageList);
        result.put("more", more);

        return result;
    }

    public Map<String, Object> findAllPicture(Integer page, Integer pageSize) {

        Map<String, Object> result = new HashMap<>();

        // 전체 50개의 페스티벌 데이터를 생성
        List<Map<String, Object>> allPictures = new ArrayList<>();
        Random random = new Random();
        // 예시로 사용할 이름 목록
        String[] names = { "Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace", "Heidi", "Ivan", "Judy" };
        String[] images = { //
                "https://www.apparelnews.co.kr/upfiles/manage/202010/fec559517a1bd71b6ffc3d4428401c07.jpg", //
                "https://static.nike.com/a/images/f_auto/dpr_3.0,cs_srgb/h_484,c_limit/c13d3272-66a8-4b73-bf90-b386b1bac95b/%EC%99%84%EB%B2%BD%ED%95%9C-%EB%9F%AC%EB%8B%9D%EC%9D%84-%EC%99%84%EC%84%B1%ED%95%98%EB%8A%94-%EB%B0%A9%EB%B2%95.jpg",
                "https://as1.ftcdn.net/jpg/02/51/13/00/1000_F_251130019_OH9dIOh3l34Pu780AHLkckmNiM2Lkcjz.jpg", //
                "https://marathonhandbook.com/wp-content/uploads/2022/10/Running-Coach-Certification.jpg" //
        };

        for (int i = 0; i < 50; i++) {
            Map<String, Object> picture = new HashMap<>();

            // 대회ID (자동 증가 값)
            picture.put("pictureId", "FST" + (i + 1));

            // 날짜: 2020년부터 2025년 사이의 랜덤 날짜 (월/일은 간단하게 1~28 사이 선택)
            int year = random.nextInt(6) + 2020; // 2020 ~ 2025
            int month = random.nextInt(12) + 1; // 1 ~ 12
            int day = random.nextInt(28) + 1; // 1 ~ 28
            String date = String.format("%04d-%02d-%02d", year, month, day);
            picture.put("date", date);

            // 대회명
            picture.put("pictureName", "picture " + (i + 1));

            // 등록자명: 미리 정의된 이름 목록에서 랜덤 선택
            String registerName = names[random.nextInt(names.length)];
            picture.put("registerName", registerName);

            // 등록자 사진 URL: 예시 URL (미리 정의된 이미지 목록 중 랜덤 선택)
            String registerImage = images[random.nextInt(images.length)];
            picture.put("registerPhotoId", "PTO" + (i + 1));
            picture.put("registerPhotoUrl", registerImage);

            allPictures.add(picture);
        }

        // 페이징 처리
        int total = allPictures.size();
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);

        // startIndex가 전체 데이터 범위를 벗어나지 않는지 확인
        List<Map<String, Object>> pageList = new ArrayList<>();
        if (startIndex < total) {
            pageList = allPictures.subList(startIndex, endIndex);
        }
        // 더보기 여부: 현재 페이지의 마지막 인덱스가 전체 데이터 개수보다 작은 경우
        boolean more = endIndex < total;

        result.put("list", pageList);
        result.put("more", more);

        return result;
    }

}
