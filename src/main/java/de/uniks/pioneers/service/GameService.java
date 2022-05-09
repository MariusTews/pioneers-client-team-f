package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.CreateGameDto;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.rest.GamesApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class GameService {

    private final GamesApiService gamesApiService;

    @Inject
    public GameService(GamesApiService gamesApiService) {
        this.gamesApiService = gamesApiService;
    }

    public Observable<Game> create(String gameName, String password) {
        return gamesApiService
                .create(new CreateGameDto(gameName, password));
    }
}
