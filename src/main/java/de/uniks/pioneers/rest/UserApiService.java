package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.CreateUserDto;
import de.uniks.pioneers.model.User;
import retrofit2.http.*;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;

public interface UserApiService {
    @POST("users")
    Observable<User> createUser(@Body CreateUserDto dto);

    @GET("users")
    Observable<List<User>> findAllUsers();

    @GET("users/{id}")
    Observable<User> findUser(@Path("id") String id);

    @PATCH("users/{id}")
    Observable<User> updateUser(@Path("id") String id);

    @DELETE("users/{id}")
    Observable<User> deleteUser(@Path("id") String id);
}
