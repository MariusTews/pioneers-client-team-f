package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.CreateUserDto;
import de.uniks.pioneers.model.User;
import retrofit2.http.*;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;

public interface UserApiService {
    @POST("users")
    Observable<User> create(@Body CreateUserDto dto);

    @GET("users")
    Observable<List<User>> findAll();

    @GET("users/{id}")
    Observable<User> findOne(@Path("id") String id);
}
