package com.colon.mattfolio.database.account.entity;

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

    private static final long serialVersionUID = -818442405L;

    public static final QAccountTokenEntity accountTokenEntity = new QAccountTokenEntity("accountTokenEntity");

    public final com.colon.mattfolio.database.common.QBaseTimeEntity _super = new com.colon.mattfolio.database.common.QBaseTimeEntity(this);

    public final StringPath accessToken = createString("accessToken");

    public final NumberPath<Long> accountId = createNumber("accountId", Long.class);

    //inherited
    public final NumberPath<Long> createdAccountId = _super.createdAccountId;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    //inherited
    public final NumberPath<Long> modifiedAccountId = _super.modifiedAccountId;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

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

