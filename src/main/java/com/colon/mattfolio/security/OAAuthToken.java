package com.colon.mattfolio.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.User;

public class OAAuthToken extends AbstractAuthenticationToken {

    private final String token;
    private final User user;

    public OAAuthToken(String token) {
        super(null);

        this.token = token;
        this.user = null;
        setAuthenticated(false);
    }

    public OAAuthToken(String token, User user) {
        super(user.getAuthorities());

        this.token = token;
        this.user = user;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return getToken();
    }

    @Override
    public Object getPrincipal() {
        return getUser();
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }
}
