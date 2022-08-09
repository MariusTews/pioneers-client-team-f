package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.ErrorResponse;
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
import static org.testfx.assertions.api.Assertions.assertThat;

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
                "now", "123", "name",
                "status", "avatar", new ArrayList<>(),
                "accessToken", "refreshToken")));

        final LoginResult result = authService.login("username", "password").blockingFirst();
        assertEquals("123", result._id());
        assertEquals("accessToken", tokenStorage.getToken());
        assertEquals("refreshToken", refreshTokenStorage.getRefreshToken());
        assertEquals("123", idStorage.getID());
        assertEquals("01.01.2022-0:00", result.createdAt());
        assertEquals("now", result.updatedAt());
        assertEquals("name", result.name());
        assertEquals("status", result.status());
        assertEquals("avatar", result.avatar());
        assertThat(result.friends()).isNotNull();
        verify(authApiService).login(new LoginDto("username", "password"));
    }

    @Test
    void logoutErrorTest() {
        when(authApiService.logout()).thenReturn(Observable.just(new ErrorResponse(123, "NO", "You are not allowed to log out!")));
        final ErrorResponse response = authService.logout().blockingFirst();
        assertEquals(response.statusCode(), 123);
        assertEquals(response.error(), "NO");
        assertEquals(response.message(), "You are not allowed to log out!");
        verify(authApiService).logout();
    }
}
