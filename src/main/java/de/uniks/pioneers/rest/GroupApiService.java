package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.CreateGroupDto;
import de.uniks.pioneers.model.Group;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

@SuppressWarnings({"SameReturnValue", "unused"})
public interface GroupApiService {

    @GET("groups")
    Observable<List<Group>> getAll();

    @POST("groups")
    Observable<Group> create(@Body CreateGroupDto dto);

    @GET("groups/{id}")
    Observable<Group> getOne(@Path("id") String id);

    @PATCH("groups/{id}")
    Observable<Group> patch(@Path("id") String id);

    @DELETE("groups/{id}")
    Observable<Group> delete(@Path("id") String id);
}
