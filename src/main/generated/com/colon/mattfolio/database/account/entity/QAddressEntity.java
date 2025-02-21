package com.colon.mattfolio.database.account.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAddressEntity is a Querydsl query type for AddressEntity
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QAddressEntity extends BeanPath<AddressEntity> {

    private static final long serialVersionUID = 1736427755L;

    public static final QAddressEntity addressEntity = new QAddressEntity("addressEntity");

    public final StringPath addressDetail = createString("addressDetail");

    public final StringPath roadAddress = createString("roadAddress");

    public final StringPath zipcode = createString("zipcode");

    public QAddressEntity(String variable) {
        super(AddressEntity.class, forVariable(variable));
    }

    public QAddressEntity(Path<? extends AddressEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAddressEntity(PathMetadata metadata) {
        super(AddressEntity.class, metadata);
    }

}

