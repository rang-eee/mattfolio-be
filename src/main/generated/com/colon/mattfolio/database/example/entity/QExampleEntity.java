package com.colon.mattfolio.database.example.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QExampleEntity is a Querydsl query type for ExampleEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QExampleEntity extends EntityPathBase<ExampleEntity> {

    private static final long serialVersionUID = -485024060L;

    public static final QExampleEntity exampleEntity = new QExampleEntity("exampleEntity");

    public final com.colon.mattfolio.database.common.QBaseTimeEntity _super = new com.colon.mattfolio.database.common.QBaseTimeEntity(this);

    public final NumberPath<Long> accountId = createNumber("accountId", Long.class);

    public final NumberPath<Integer> age = createNumber("age", Integer.class);

    //inherited
    public final NumberPath<Long> createdAccountId = _super.createdAccountId;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    //inherited
    public final NumberPath<Long> modifiedAccountId = _super.modifiedAccountId;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final StringPath name = createString("name");

    public QExampleEntity(String variable) {
        super(ExampleEntity.class, forVariable(variable));
    }

    public QExampleEntity(Path<? extends ExampleEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QExampleEntity(PathMetadata metadata) {
        super(ExampleEntity.class, metadata);
    }

}

