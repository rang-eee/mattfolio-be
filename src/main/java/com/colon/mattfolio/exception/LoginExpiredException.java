package com.colon.mattfolio.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class LoginExpiredException extends OAuth2Exception {

    public LoginExpiredException(String msg) {
        super(msg);
    }
}
