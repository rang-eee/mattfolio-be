package com.colon.mattfolio.security;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.colon.mattfolio.model.AccountPrincipalDto;
import com.colon.mattfolio.model.AccountRequestDto;
import com.colon.mattfolio.service.AccountService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service("UserDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    // private final AccountRepository accountRepository;
    private final AccountService accountService;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String arg0) throws AuthenticationException {
        String username = arg0.substring(0, arg0.lastIndexOf("@"));
        String companyCode = arg0.substring(arg0.lastIndexOf("@") + 1);

        /* JPA로 사용자 정보 조회 */
        // Restrictions r = new Restrictions();
        // r.ilike("username", username);
        // if (StringUtils.isNotNullEmpty(companyCode)) {
        // r.eq("company.code", companyCode);
        // }

        // Account account = accountRepository.findOne(r.output())
        // .orElse(null);
        // if (account == null)
        // throw new UsernameNotFoundException("");

        // if (account.getPasswordLockCount() > 5)
        // throw new LockedException("5회 이상 비밀번호가 일치하지 않아 계정이 잠겼습니다.");
        /* //JPA로 사용자 정보 조회 */

        /* Mybatis로 사용자 정보 조회 */
        AccountRequestDto requestDto = AccountRequestDto.builder()
            .companyCode(companyCode) // 법인 코드도 필수로 조회
            // .companyCode(StringUtils.isNullEmpty(companyCode) ? null : companyCode)
            .userId(username)
            .useYn("Y")
            .build();

        AccountPrincipalDto account = accountService.findAccountPrincipal(requestDto);
        if (account == null)
            throw new UsernameNotFoundException("");

        if (account.getPasswordLockCount() > 5)
            throw new LockedException("5회 이상 비밀번호가 일치하지 않아 계정이 잠겼습니다.");

        if ("Y".equals(account.getLockYn()))
            throw new LockedException("계정이 잠겨있어 사용할 수 없습니다.");

        /* //Mybatis로 사용자 정보 조회 */
        // return new AccountUser(account);
        return new AccountUser(account);
    }

}
