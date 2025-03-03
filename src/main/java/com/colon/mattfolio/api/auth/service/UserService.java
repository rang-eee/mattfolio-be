package com.colon.mattfolio.api.auth.service;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.colon.mattfolio.api.auth.dto.SignUpRequest;
import com.colon.mattfolio.database.account.entity.AccountEntity;
import com.colon.mattfolio.database.account.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final AccountRepository accountRepository;

    // @Transactional
    // public String createUser(SignUpRequest signUpRequest) {
    // if (accountRepository.existsByAuthProviderAndProviderId(signUpRequest.getAuthProvider(), signUpRequest.getId())) {
    // throw new BadRequestException("aleady exist user");
    // }

    // return accountRepository.save(AccountEntity.builder()
    // .providerId(signUpRequest.getId())
    // .nickname(signUpRequest.getNickname())
    // .email(signUpRequest.getEmail())
    // .profileImageUrl(signUpRequest.getProfileImageUrl())
    // .role(Role.USER)
    // .authProvider(signUpRequest.getAuthProvider())
    // .build())
    // .getId();
    // }
}
