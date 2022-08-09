package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.CreateMapTemplateDto;
import de.uniks.pioneers.template.MapTemplate;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

public interface MapsApiService {
    @POST("maps")
    Observable<MapTemplate> createMapTemplate(@Body CreateMapTemplateDto dto);

    @GET("maps")
    Observable<List<MapTemplate>> findAllMaps();

    @DELETE("maps/{id}")
    Observable<MapTemplate> delete(@Path("id") String id);
}
