package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.model.*;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

public interface PioneersApiService {

	@GET("games/{gameId}/map")
	Observable<Map> findAllTiles(@Path("gameId") String gameId);

	@GET("games/{gameId}/players")
	Observable<List<Player>> findAllPlayers(@Path("gameId") String gameId);

	@GET("games/{gameId}/players/{userId}")
	Observable<Player> findOnePlayer(@Path("gameId") String gameId,
									 @Path("userId") String userId);

	@PATCH("games/{gameId}/players/{userId}")
	Observable<Player> updatePlayer(@Path("gameId") String gameId, @Path("userId") String userId);

	@GET("games/{gameId}/state")
	Observable<State> findOneState(@Path("gameId") String gameId);

	@GET("games/{gameId}/buildings")
	Observable<List<Building>> findAllBuildings(@Path("gameId") String gameId);

	@GET("games/{gameId}/buildings/{buildingId}")
	Observable<Building> findOneBuilding(@Path("gameId") String gameId,
										 @Path("buildingId") String buildingId);

	@POST("games/{gameId}/moves")
	Observable<Move> create(@Path("gameId") String gameId,
							@Body CreateMoveDto dto);

	@GET("games/{gameId}/moves")
	Observable<List<Move>> findAllMoves(@Path("gameId") String gameId);

	@GET("games/{gameId}/moves/{moveId}")
	Observable<Move> findOneMove(@Path("gameId") String gameId, @Path("moveId") String moveId);
}
