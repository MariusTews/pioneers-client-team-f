package de.uniks.pioneers.service;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RefreshTokenStorage {

    public String refreshToken;

    @Inject
    public RefreshTokenStorage(){
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
