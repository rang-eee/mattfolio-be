package com.colon.mattfolio.mapper;

import com.colon.mattfolio.model.AccountPrincipalDto;
import com.colon.mattfolio.model.AccountRequestDto;

/**
 * AccountMapper 인터페이스
 * 
 */
// @Mapper
public interface AccountMapper {

    /**
     * Account Principal 정보를 조회합니다.
     *
     * @param request 조건 요청 객체
     * @return Account Principal 정보
     */
    AccountPrincipalDto findAccountPrincipal(AccountRequestDto request);

}
