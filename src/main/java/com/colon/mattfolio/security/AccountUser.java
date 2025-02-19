package com.colon.mattfolio.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.colon.mattfolio.model.AccountPrincipalDto;
import com.google.common.collect.Sets;

import lombok.Getter;

@Getter
public class AccountUser extends User {

    private final AccountPrincipalDto account;

    // public AccountUser(Account account) {
    public AccountUser(AccountPrincipalDto account) {
        super(parseUsername(account), StringUtils.isEmpty(account.getPassword()) ? "_" : account.getPassword(), authorities(account));
        this.account = account;
    }

    private static String parseUsername(AccountPrincipalDto account) {
        return account.getUserId() + "@" + (account.getCompanyCode() == null ? "" : account.getCompanyCode());
    }

    private static Collection<? extends GrantedAuthority> authorities(AccountPrincipalDto account) {
        Set<String> authorities = Sets.newHashSet();
        authorities.add("ROLE_USER");

        String roleTypeCode = account.getAccountRoleTypeCode()
            .toString();

        if (!StringUtils.equals("USER", roleTypeCode)) {
            authorities.add("ROLE_" + roleTypeCode);
        }

        return authorities.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }

}
