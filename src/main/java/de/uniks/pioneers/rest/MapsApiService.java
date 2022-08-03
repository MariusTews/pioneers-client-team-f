package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.CreateVoteDto;
import de.uniks.pioneers.model.Vote;
import de.uniks.pioneers.template.MapTemplate;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

public interface MapsApiService {
    @GET("maps")
    Observable<List<MapTemplate>> findAllMaps();

    @DELETE("maps/{id}")
    Observable<MapTemplate> delete(@Path("id") String id);

    @POST("maps/{id}/votes")
    Observable<Vote> vote(@Path("id") String id, @Body CreateVoteDto dto);

    @DELETE("maps/{mapId}/votes/{userId}")
    Observable<Vote> deleteVote(@Path("mapId") String mapId, @Path("userId") String userId);
}
