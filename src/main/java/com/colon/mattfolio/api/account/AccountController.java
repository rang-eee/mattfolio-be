package com.colon.mattfolio.api.account;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.colon.mattfolio.api.account.dto.AccountDto;
import com.colon.mattfolio.api.account.dto.MemberEditRequest;
import com.colon.mattfolio.common.annotation.RoleUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/account")
@RequiredArgsConstructor
@RestController
public class AccountController {

	private final AccountService accountService;

	@RoleUser
	@GetMapping
	public ResponseEntity<AccountDto> memberInfo(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(accountService.memberInfo(userDetails.getUsername()));
	}

	@RoleUser
	@PatchMapping
	public ResponseEntity<AccountDto> memberEdit(@RequestBody @Valid MemberEditRequest request, @AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(accountService.memberEdit(request, userDetails.getUsername()));
	}
}
