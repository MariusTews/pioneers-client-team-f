package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.CreateMapTemplateDto;
import de.uniks.pioneers.dto.CreateVoteDto;
import de.uniks.pioneers.model.Vote;
import de.uniks.pioneers.template.MapTemplate;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

@SuppressWarnings({"unused", "SameReturnValue"})
public interface MapsApiService {
    @POST("maps")
    Observable<MapTemplate> createMapTemplate(@Body CreateMapTemplateDto dto);

    @GET("maps")
    Observable<List<MapTemplate>> findAllMaps();

    @GET("maps/{id}/votes")
    Observable<List<Vote>> findVotesByMapId(@Path("id") String id);

    @DELETE("maps/{id}")
    Observable<MapTemplate> delete(@Path("id") String id);

    @POST("maps/{id}/votes")
    Observable<Vote> vote(@Path("id") String id, @Body CreateVoteDto dto);

    @DELETE("maps/{mapId}/votes/{userId}")
    Observable<Vote> deleteVote(@Path("mapId") String mapId, @Path("userId") String userId);
}
