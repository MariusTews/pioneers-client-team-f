package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.LoginDto;
import de.uniks.pioneers.dto.LoginResult;
import de.uniks.pioneers.rest.AuthApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class LoginService {

    private final AuthApiService authApiService;
    private final TokenStorage tokenStorage;
    private final IDStorage idStorage;

    @Inject
    public LoginService(AuthApiService authApiService,
                        TokenStorage tokenStorage,
                        IDStorage idStorage) {
        this.authApiService = authApiService;
        this.tokenStorage = tokenStorage;
        this.idStorage = idStorage;
    }

    public Observable<LoginResult> login(String username, String password) {
        return authApiService
                .login(new LoginDto(username, password))
                .doOnNext(result -> {
                    tokenStorage.setToken(result.accessToken());
                    idStorage.setID(result._id());
                });
    }
}
