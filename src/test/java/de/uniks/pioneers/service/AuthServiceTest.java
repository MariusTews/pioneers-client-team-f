package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.LoginDto;
import de.uniks.pioneers.dto.LoginResult;
import de.uniks.pioneers.rest.AuthApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Spy
    TokenStorage tokenStorage;

    @Spy
    IDStorage idStorage;

    @Spy
    RefreshTokenStorage refreshTokenStorage;

    @Mock
    AuthApiService authApiService;

    @InjectMocks
    AuthService authService;

    @Test
    void loginTest() {
        when(authApiService.login(any())).thenReturn(Observable.just(new LoginResult("01.01.2022-0:00",
                                                                "now","123", "name",
                                                                    "status", "avatar", new ArrayList<>(),
                                                                "accessToken", "refreshToken")));

        final LoginResult result = authService.login("username", "password").blockingFirst();
        assertEquals("123", result._id());

        assertEquals("accessToken", tokenStorage.getToken());

        assertEquals("refreshToken", refreshTokenStorage.getRefreshToken());

        assertEquals("123", idStorage.getID());

        verify(authApiService).login(new LoginDto("username", "password"));
    }
}
