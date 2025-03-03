package com.colon.mattfolio.database.account.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAccountEntity is a Querydsl query type for AccountEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAccountEntity extends EntityPathBase<AccountEntity> {

    private static final long serialVersionUID = 1607755876L;

    public static final QAccountEntity accountEntity = new QAccountEntity("accountEntity");

    public final com.colon.mattfolio.database.common.QBaseTimeEntity _super = new com.colon.mattfolio.database.common.QBaseTimeEntity(this);

    public final NumberPath<Long> accountId = createNumber("accountId", Long.class);

    //inherited
    public final NumberPath<Long> createdAccountId = _super.createdAccountId;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath email = createString("email");

    public final EnumPath<com.colon.mattfolio.common.enumType.LoginAuthProvider> loginAuthProvider = createEnum("loginAuthProvider", com.colon.mattfolio.common.enumType.LoginAuthProvider.class);

    public final StringPath loginAuthProviderId = createString("loginAuthProviderId");

    //inherited
    public final NumberPath<Long> modifiedAccountId = _super.modifiedAccountId;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final StringPath name = createString("name");

    public final StringPath profileImgUrl = createString("profileImgUrl");

    public final EnumPath<com.colon.mattfolio.common.enumType.AccountRoleType> role = createEnum("role", com.colon.mattfolio.common.enumType.AccountRoleType.class);

    public QAccountEntity(String variable) {
        super(AccountEntity.class, forVariable(variable));
    }

    public QAccountEntity(Path<? extends AccountEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAccountEntity(PathMetadata metadata) {
        super(AccountEntity.class, metadata);
    }

}

