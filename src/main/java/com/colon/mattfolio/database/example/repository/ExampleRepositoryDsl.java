package com.colon.mattfolio.database.example.repository;

import java.util.List;
import com.colon.mattfolio.database.example.entity.ExampleEntity;

/**
 * ExampleRepositoryDsl 인터페이스는 Querydsl을 사용하여 ExampleEntity에 대한 커스텀 쿼리 메서드를 정의합니다. <br/>
 * 이 인터페이스를 구현한 클래스에서 동적 쿼리를 작성할 수 있습니다.
 */
public interface ExampleRepositoryDsl {

    /**
     * 모든 ExampleEntity 데이터를 조회하는 커스텀 메서드입니다. <br/>
     * 
     * @return ExampleEntity 객체의 리스트
     */
    List<ExampleEntity> findAllByExample();

    /**
     * 주어진 이름에 해당하는 ExampleEntity를 조회하는 커스텀 메서드입니다. <br/>
     * 
     * @param name 조회할 이름
     * @return 해당 이름에 해당하는 ExampleEntity (없으면 null)
     */
    ExampleEntity findByExample(String name);
}
