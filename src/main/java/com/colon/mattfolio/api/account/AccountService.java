package com.colon.mattfolio.api.account;

import static com.colon.mattfolio.common.exception.ErrorCode.MEMBER_NOT_FOUND;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.colon.mattfolio.api.account.dto.AccountDto;
import com.colon.mattfolio.api.account.dto.MemberEditRequest;
import com.colon.mattfolio.api.account.exception.AccountException;
import com.colon.mattfolio.database.account.entity.AccountEntity;
import com.colon.mattfolio.database.account.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountDto memberInfo(String memberKey) {
        AccountEntity member = findByMemberKeyOrThrow(memberKey);
        return AccountDto.fromEntity(member);
    }

    @Transactional
    public AccountDto memberEdit(MemberEditRequest request, String memberKey) {
        AccountEntity member = findByMemberKeyOrThrow(memberKey);
        member.updateMember(request);
        return AccountDto.fromEntity(member);
    }

    private AccountEntity findByMemberKeyOrThrow(String memberKey) {
        return accountRepository.findByMemberKey(memberKey)
            .orElseThrow(() -> new AccountException(MEMBER_NOT_FOUND));
    }
}
