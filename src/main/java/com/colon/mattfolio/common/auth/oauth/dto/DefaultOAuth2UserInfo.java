package com.colon.mattfolio.common.auth.oauth.dto;

public class DefaultOAuth2UserInfo implements OAuth2UserInfo {

    private final String provider;
    private final String email;
    private final String name;
    private final String profile;

    public DefaultOAuth2UserInfo(String provider, String email, String name, String profile) {
        this.provider = provider;
        this.email = email;
        this.name = name;
        this.profile = profile;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getProvider() {
        return provider;
    }

    @Override
    public String getProfile() {
        return profile;
    }
}
