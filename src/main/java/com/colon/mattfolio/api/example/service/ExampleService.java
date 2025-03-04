package com.colon.mattfolio.api.example.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.colon.mattfolio.database.example.entity.ExampleEntity;
import com.colon.mattfolio.database.example.repository.ExampleRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ExampleService {
    // 기존 JPA Repository (필요 시 사용)
    private final ExampleRepository exampleRepository;

    /**
     * 모든 예제 데이터를 조회합니다. <br/>
     * 
     * @return ExampleEntity 객체의 리스트
     */
    public List<ExampleEntity> findAllExamples() {
        return exampleRepository.findAllByExample();
    }

    /**
     * 주어진 이름에 해당하는 예제 데이터를 조회합니다. <br/>
     * 
     * @param name 조회할 예제의 이름
     * @return 해당 이름을 가진 ExampleEntity 객체 (없으면 null)
     */
    public ExampleEntity findExampleByName(String name) {
        return exampleRepository.findByExample(name);
    }
}
