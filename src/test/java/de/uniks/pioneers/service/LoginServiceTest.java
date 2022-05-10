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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {
    @Spy
    TokenStorage tokenStorage;

    @Spy
    IDStorage idStorage;

    @Mock
    AuthApiService authApiService;

    @InjectMocks
    LoginService loginService;

    @Test
    void loginTest() {
        when(authApiService.login(any())).thenReturn(Observable.just(new LoginResult("123", "name", "status", "avatar", "accessToken", "refreshToken")));

        final String result = loginService.login("username", "password").blockingFirst();
        assertEquals("123", result);

        assertEquals("accessToken", tokenStorage.getToken());

        assertEquals("123", idStorage.getID());

        verify(authApiService).login(new LoginDto("username", "password"));
    }
}
