package de.uniks.pioneers.rest;

import de.uniks.pioneers.Template.MapTemplate;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface MapsApiService {
    @GET("maps")
    Observable<List<MapTemplate>> findAllMaps();
}
