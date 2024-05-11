package com.mrsisa.pharmacy.aspect;

import com.mrsisa.pharmacy.domain.entities.BaseEntity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OwnsEntity {
    String entityId() default "id";
    String ownerField();
    Class<? extends BaseEntity> entity();
}
