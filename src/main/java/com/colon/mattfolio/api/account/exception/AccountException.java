package com.colon.mattfolio.api.account.exception;

import com.colon.mattfolio.common.exception.CustomException;
import com.colon.mattfolio.common.exception.ErrorCode;

public class AccountException extends CustomException {

    public AccountException(ErrorCode errorCode) {
        super(errorCode);
    }
}
