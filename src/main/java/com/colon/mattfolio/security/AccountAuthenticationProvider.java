package com.colon.mattfolio.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.colon.mattfolio.model.AccountLoginDto;
import com.colon.mattfolio.model.AccountPrincipalDto;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AccountAuthenticationProvider implements AuthenticationProvider {

    @Qualifier("UserDetailsService")
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    // private final APIVendorService apiVendorService;

    private final WebClient webClient;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        AccountUser user = (AccountUser) userDetailsService.loadUserByUsername(username);
        AccountPrincipalDto userAccount = user.getAccount();

        if (userAccount != null && "Y".equals(userAccount.getApiVendorUseYnTemp())) {
            String loginUrl = "http://10.60.233.115:8080/accounts/login"; // api vendor 테이블에 하드코딩 되어 있던 값
            try {
                AccountLoginDto payload = AccountLoginDto.builder()
                    .username(userAccount.getUserId())
                    .password(password)
                    .build();

                webClient.post()
                    .uri(loginUrl)
                    .body(Mono.just(payload), AccountLoginDto.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

                System.out.println("Finish OK");
                return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new UsernameNotFoundException(ex.getMessage());
            }
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UsernameNotFoundException("");
        }

        return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
    }

    // @Override
    // public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    // String username = authentication.getName();
    // String password = (String) authentication.getCredentials();

    // AccountUser user = (AccountUser) userDetailsService.loadUserByUsername(username);
    // if (user.getAccount().getApiVendor() != null) {

    // APIVendorDto.Simple api = user.getAccount().getApiVendor();
    // APIVendorDto.Details apiDetails = apiVendorService.findOne(api.getId(), null);

    // String loginUrl = apiDetails.getEndpoints().get("login");
    // try {
    // AccountDto.Login payload = new AccountDto.Login();
    // payload.setUsername(user.getAccount().getUserId());
    // payload.setPassword(password);

    // webClient.post()
    // .uri(loginUrl)
    // .body(Mono.just(payload), AccountDto.Login.class)
    // .retrieve()
    // .bodyToMono(String.class)
    // .block();

    // System.out.println("Finish OK");
    // return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
    // } catch (Exception ex) {
    // ex.printStackTrace();
    // throw new UsernameNotFoundException(ex.getMessage());
    // }
    // }

    // if (!passwordEncoder.matches(password, user.getPassword())) {
    // throw new UsernameNotFoundException("");
    // }

    // return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
    // }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
