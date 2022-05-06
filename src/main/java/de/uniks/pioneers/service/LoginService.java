package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.LoginDto;
import de.uniks.pioneers.dto.LoginResult;
import de.uniks.pioneers.rest.AuthApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class LoginService {

    private final AuthApiService authApiService;
    private final TokenStorage tokenStorage;

    @Inject
    public LoginService(AuthApiService authApiService, TokenStorage tokenStorage) {
        this.authApiService = authApiService;
        this.tokenStorage = tokenStorage;
    }

    public Observable<String> login(String username, String password) {
        return authApiService
                .login(new LoginDto(username, password))
                .doOnNext(result -> tokenStorage.setToken(result.accessToken()))
                .map(LoginResult::name);
    }
}
