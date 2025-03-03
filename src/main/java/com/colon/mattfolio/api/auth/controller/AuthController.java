package com.colon.mattfolio.api.auth.controller;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.colon.mattfolio.api.auth.dto.RefreshTokenRequest;
import com.colon.mattfolio.api.auth.dto.SignInResponse;
import com.colon.mattfolio.api.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

// @CrossOrigin(origins = "http://about:blank")
@RestController("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login/oauth2/code/{registrationId}")
    public ResponseEntity<SignInResponse> redirect(//
            @PathVariable("registrationId") String registrationId, //
            @RequestParam("code") String code, //
            @RequestParam("state") String state) {
        try {
            return ResponseEntity.ok(authService.redirect(RefreshTokenRequest.builder()
                .registrationId(registrationId)
                .code(code)
                .state(state)
                .build()));
        } catch (BadRequestException e) {
            e.printStackTrace();
            return ResponseEntity.ok(SignInResponse.builder()
                .accessToken(null)
                .refreshToken(null)
                .build());
        }
    }

    @PostMapping("/auth/token")
    public ResponseEntity<SignInResponse> refreshToken(@RequestBody RefreshTokenRequest tokenRequest) {
        try {
            return ResponseEntity.ok(authService.refreshToken(tokenRequest));
        } catch (BadRequestException e) {
            e.printStackTrace();
            return ResponseEntity.ok(SignInResponse.builder()
                .accessToken(null)
                .refreshToken(null)
                .build());
        }

    }
}