package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.ErrorResponse;
import de.uniks.pioneers.dto.LoginDto;
import de.uniks.pioneers.dto.LoginResult;
import de.uniks.pioneers.dto.RefreshDto;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

@SuppressWarnings({"SameReturnValue", "unused"})
public interface AuthApiService {

    @POST("auth/login")
    Observable<LoginResult> login(@Body LoginDto dto);

    @POST("auth/refresh")
    Observable<LoginResult> refresh(@Body RefreshDto dto);

    @POST("auth/logout")
    Observable<ErrorResponse> logout();
}
