package com.colon.mattfolio.api.account.contoller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.colon.mattfolio.api.account.service.AccountService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/account")
@RequiredArgsConstructor
@RestController
public class AccountController {

    private final AccountService accountService;

}
