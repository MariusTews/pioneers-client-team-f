package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.ErrorResponse;
import de.uniks.pioneers.dto.LoginDto;
import de.uniks.pioneers.dto.LoginResult;
import de.uniks.pioneers.dto.RefreshDto;
import de.uniks.pioneers.rest.AuthApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class AuthService {

    private final AuthApiService authApiService;
    private final TokenStorage tokenStorage;
    private final IDStorage idStorage;

    private final RefreshTokenStorage refreshTokenStorage;

    @Inject
    public AuthService(AuthApiService authApiService,
                       TokenStorage tokenStorage,
                       IDStorage idStorage,
                       RefreshTokenStorage refreshTokenStorage) {
        this.authApiService = authApiService;
        this.tokenStorage = tokenStorage;
        this.idStorage = idStorage;
        this.refreshTokenStorage = refreshTokenStorage;
    }

    public Observable<LoginResult> login(String username, String password) {
        return authApiService
                .login(new LoginDto(username, password))
                .doOnNext(result -> {
                    tokenStorage.setToken(result.accessToken());
                    idStorage.setID(result._id());
                    refreshTokenStorage.setRefreshToken(result.refreshToken());
                });
    }

    public Observable<LoginResult> refreshToken(String refreshToken) {
        return authApiService.refresh(new RefreshDto(refreshToken))
                .doOnNext(result -> {
                    tokenStorage.setToken(result.accessToken());
                    idStorage.setID((result._id()));
                    refreshTokenStorage.setRefreshToken(result.refreshToken());
                });
    }

    public Observable<ErrorResponse> logout() {
        return authApiService
                .logout();
    }
}
