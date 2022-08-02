package de.uniks.pioneers.rest;

import de.uniks.pioneers.template.MapTemplate;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface MapsApiService {
    @GET("maps")
    Observable<List<MapTemplate>> findAllMaps();

    @DELETE("maps/{id}")
    Observable<MapTemplate> delete(@Path("id") String id);
}
