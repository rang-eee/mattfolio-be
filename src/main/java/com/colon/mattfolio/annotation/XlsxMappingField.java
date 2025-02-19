package com.colon.mattfolio.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * xlsx 생성시 매핑 될 컬럼 정의
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XlsxMappingField {
    /**
     * 컬럼 명칭
     * 
     * @return
     */
    String column() default "";

    /**
     * 컬럼 순서 (0부터 시작)
     * 
     * @return
     */
    int index();
}
