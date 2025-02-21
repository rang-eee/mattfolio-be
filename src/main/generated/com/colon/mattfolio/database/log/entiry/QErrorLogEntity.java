package com.colon.mattfolio.database.log.entiry;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QErrorLogEntity is a Querydsl query type for ErrorLogEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QErrorLogEntity extends EntityPathBase<ErrorLogEntity> {

    private static final long serialVersionUID = -792587136L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QErrorLogEntity errorLogEntity = new QErrorLogEntity("errorLogEntity");

    public final com.colon.mattfolio.database.common.QBaseTimeEntity _super = new com.colon.mattfolio.database.common.QBaseTimeEntity(this);

    public final com.colon.mattfolio.database.account.entity.QAddressEntity address;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memberKey = createString("memberKey");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final StringPath name = createString("name");

    public final StringPath profile = createString("profile");

    public final EnumPath<com.colon.mattfolio.database.account.entity.Role> role = createEnum("role", com.colon.mattfolio.database.account.entity.Role.class);

    public QErrorLogEntity(String variable) {
        this(ErrorLogEntity.class, forVariable(variable), INITS);
    }

    public QErrorLogEntity(Path<? extends ErrorLogEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QErrorLogEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QErrorLogEntity(PathMetadata metadata, PathInits inits) {
        this(ErrorLogEntity.class, metadata, inits);
    }

    public QErrorLogEntity(Class<? extends ErrorLogEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.address = inits.isInitialized("address") ? new com.colon.mattfolio.database.account.entity.QAddressEntity(forProperty("address")) : null;
    }

}

