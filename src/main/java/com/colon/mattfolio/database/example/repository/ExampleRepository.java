package com.colon.mattfolio.database.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.colon.mattfolio.database.example.entity.ExampleEntity;

/**
 * ExampleRepository 인터페이스는 ExampleEntity에 대한 기본 CRUD 작업과 <br/>
 * 커스텀 DSL 쿼리 기능(ExampleRepositoryDsl)을 제공하는 Repository입니다.<br/>
 */
public interface ExampleRepository extends JpaRepository<ExampleEntity, Long>, ExampleRepositoryDsl {

}
