package com.colon.mattfolio.filter;

import java.io.IOException;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.colon.mattfolio.security.OAAuthToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ExtraAuthFilter extends AbstractAuthenticationProcessingFilter {

    public static final String OA_TOKEN_HEADER = "tq-oa-token";

    public ExtraAuthFilter(RequestMatcher requestMatcher) {
        super(requestMatcher);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final String oaToken = getOaTokenValue((HttpServletRequest) request);

        if (StringUtils.isEmpty(oaToken)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            this.setAuthenticationSuccessHandler((request1, response1, authentication) -> chain.doFilter(request1, response1));
        } catch (Exception ex) {
        }
        super.doFilter(request, response, chain);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        final String oaTokenValue = getOaTokenValue(request);
        if (StringUtils.isNotEmpty(oaTokenValue)) {
            OAAuthToken token = new OAAuthToken(oaTokenValue);
            token.setDetails(authenticationDetailsSource.buildDetails(request));

            return this.getAuthenticationManager()
                .authenticate(token);
        }

        return null;
    }

    private String getOaTokenValue(HttpServletRequest req) {
        return Collections.list(req.getHeaderNames())
            .stream()
            .filter(header -> header.equalsIgnoreCase(OA_TOKEN_HEADER))
            .map(req::getHeader)
            .findFirst()
            .orElse(null);
    }

}
