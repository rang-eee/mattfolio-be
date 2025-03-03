package com.colon.mattfolio.database.token.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAccountTokenEntity is a Querydsl query type for AccountTokenEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAccountTokenEntity extends EntityPathBase<AccountTokenEntity> {

    private static final long serialVersionUID = 1013035175L;

    public static final QAccountTokenEntity accountTokenEntity = new QAccountTokenEntity("accountTokenEntity");

    public final StringPath accessToken = createString("accessToken");

    public final NumberPath<Long> accountId = createNumber("accountId", Long.class);

    public final StringPath refreshToken = createString("refreshToken");

    public QAccountTokenEntity(String variable) {
        super(AccountTokenEntity.class, forVariable(variable));
    }

    public QAccountTokenEntity(Path<? extends AccountTokenEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAccountTokenEntity(PathMetadata metadata) {
        super(AccountTokenEntity.class, metadata);
    }

}

