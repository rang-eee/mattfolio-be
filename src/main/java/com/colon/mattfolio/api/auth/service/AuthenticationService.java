package com.colon.mattfolio.api.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.colon.mattfolio.common.exception.AuthException;
import com.colon.mattfolio.common.exception.ErrorCode;
import com.colon.mattfolio.database.account.entity.AccountEntity;
import com.colon.mattfolio.database.account.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final AccountRepository accountRepository;

    public AccountEntity getMemberOrThrow(String memberKey) {
        return accountRepository.findByMemberKey(memberKey)
            .orElseThrow(() -> new AuthException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public void checkAccess(String memberKey, AccountEntity member) {
        if (!member.getMemberKey()
            .equals(memberKey)) {
            throw new AuthException(ErrorCode.NO_ACCESS);
        }
    }
}
