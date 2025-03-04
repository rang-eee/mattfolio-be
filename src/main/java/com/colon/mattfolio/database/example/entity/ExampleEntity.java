package com.colon.mattfolio.database.example.entity;

import com.colon.mattfolio.database.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ExampleEntity 클래스는 MF_EXAMPLE 테이블에 매핑되는 엔티티로, 예제 데이터를 저장하기 위한 필드를 포함합니다.<br/>
 * BaseTimeEntity를 상속하여 생성일, 수정일 등의 공통 필드를 함께 관리합니다.
 */
@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MF_EXAMPLE")
public class ExampleEntity extends BaseTimeEntity {

    // 예제 엔티티의 고유 ID (PRIMARY KEY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long accountId;

    // 예제 엔티티의 이름
    @Column(name = "NAME")
    private String name;

    // 예제 엔티티의 나이
    @Column(name = "AGE")
    private Integer age;
}
