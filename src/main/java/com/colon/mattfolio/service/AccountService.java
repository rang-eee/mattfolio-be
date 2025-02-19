package com.colon.mattfolio.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.colon.mattfolio.common.base.PagingService;
import com.colon.mattfolio.mapper.AccountMapper;
import com.colon.mattfolio.model.AccountPrincipalDto;
import com.colon.mattfolio.model.AccountRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService extends PagingService {

    private final AccountMapper accountMapper;

    /**
     * Account Principal 조회
     */
    public AccountPrincipalDto findAccountPrincipal(AccountRequestDto request) {
        AccountPrincipalDto result = accountMapper.findAccountPrincipal(request);
        return result;
    }

}