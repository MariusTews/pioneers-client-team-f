package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.CreateGroupDto;
import de.uniks.pioneers.model.Groupe;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

public interface GroupeApiService {

    @GET("groups")
    Observable<List<Groupe>> getAll();

    @POST("groups")
    Observable<Groupe> create(@Body CreateGroupDto dto);

    @GET("groups/{id}")
    Observable<Groupe> getOne(@Path("id") String id);

    @PATCH("groups/{id}")
    Observable<Groupe> patch(@Path("id") String id);

    @DELETE("groups/{id}")
    Observable<Groupe> delete(@Path("id") String id);
}
