package com.colon.mattfolio.database.example.repository;

import static com.colon.mattfolio.database.example.entity.QExampleEntity.exampleEntity;

import java.util.List;

import com.colon.mattfolio.database.example.entity.ExampleEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

/**
 * ExampleRepositoryDslImpl 클래스는 ExampleRepositoryDsl 인터페이스의 구현체로, <br/>
 * Querydsl을 사용하여 ExampleEntity에 대한 커스텀 쿼리를 수행합니다.
 */
@RequiredArgsConstructor
public class ExampleRepositoryDslImpl implements ExampleRepositoryDsl {

    // Querydsl을 사용하기 위한 JPAQueryFactory
    private final JPAQueryFactory queryFactory;

    /**
     * 모든 ExampleEntity 데이터를 조회합니다.
     * 
     * @return ExampleEntity 객체들의 리스트
     */
    @Override
    public List<ExampleEntity> findAllByExample() {
        return queryFactory.selectFrom(exampleEntity)
            .fetch();
    }

    /**
     * 주어진 이름과 일치하는 ExampleEntity를 조회합니다.
     * 
     * @param name 조회할 ExampleEntity의 이름
     * @return 해당 이름에 해당하는 ExampleEntity (없으면 null 반환)
     */
    @Override
    public ExampleEntity findByExample(String name) {
        return queryFactory.selectFrom(exampleEntity)
            .where(exampleEntity.name.eq(name))
            .fetchOne();
    }
}
