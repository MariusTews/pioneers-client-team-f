package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.CreateUserDto;
import de.uniks.pioneers.dto.StatusUpdateDto;
import de.uniks.pioneers.dto.UpdateUserDto;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.model.Vote;
import retrofit2.http.*;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;

@SuppressWarnings("unused")
public interface UserApiService {
    @GET("users")
    Observable<List<User>> findAllUsers();

    @POST("users")
    Observable<User> createUser(@Body CreateUserDto dto);

    @GET("users/{id}")
    Observable<User> findUser(@Path("id") String id);

    @GET("users/{id}/votes")
    Observable<List<Vote>> findVotes(@Path("id") String id);

    @PATCH("users/{id}")
    Observable<User> updateUser(@Path("id") String id, @Body UpdateUserDto dto);

    @PATCH("users/{id}")
    Observable<User> statusUpdate(@Path("id") String id, @Body StatusUpdateDto dto);

    @DELETE("users/{id}")
    Observable<User> deleteUser(@Path("id") String id);
}
