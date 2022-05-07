package de.uniks.pioneers.service;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TokenStorage {

    private String token;

    @Inject
    public TokenStorage() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
