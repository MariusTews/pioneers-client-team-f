package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.CreateUserDto;
import de.uniks.pioneers.dto.UpdateUserDto;
import de.uniks.pioneers.model.User;
import retrofit2.http.*;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;

public interface UserApiService {

    @GET("users")
    Observable<List<User>> findAll();

    @POST("users")
    Observable<User> create(@Body CreateUserDto dto);

    @GET("users/{id}")
    Observable<User> findOne(@Path("id") String id);

    @PATCH("users/{id}")
    Observable<User> patch(@Path(("id")) String id,
                           @Body UpdateUserDto dto);

    @DELETE("users/{id}")
    Observable<User> delete(@Path("id") String id);
}
