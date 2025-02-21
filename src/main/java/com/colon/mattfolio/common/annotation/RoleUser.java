package com.colon.mattfolio.common.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

@Target({ METHOD, TYPE })
@Retention(RUNTIME)
@PreAuthorize("hasRole('USER')")
public @interface RoleUser {

}
