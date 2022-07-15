package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.CreateGameDto;
import de.uniks.pioneers.dto.UpdateGameDto;
import de.uniks.pioneers.model.Game;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

@SuppressWarnings("unused")
public interface GamesApiService {

    @GET("games")
    Observable<List<Game>> findAll();

    @POST("games")
    Observable<Game> create(@Body CreateGameDto dto);

    @GET("games/{id}")
    Observable<Game> findOne(@Path("id") String id);

    @PATCH("games/{id}")
    Observable<Game> patch(@Path("id") String id,
                           @Body UpdateGameDto dto);

    @SuppressWarnings("SameReturnValue")
    @DELETE("games/{id}")
    Observable<Game> delete(@Path("id") String id);
}
