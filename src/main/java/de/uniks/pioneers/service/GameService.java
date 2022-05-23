package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.CreateGameDto;
import de.uniks.pioneers.dto.UpdateGameDto;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.rest.GamesApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class GameService {

    private final GamesApiService gamesApiService;
    private final GameIDStorage gameIDStorage;
    private final MemberIDStorage memberIDStorage;

    @Inject
    public GameService(GamesApiService gamesApiService, GameIDStorage gameIDStorage, MemberIDStorage memberIDStorage) {
        this.gamesApiService = gamesApiService;
        this.gameIDStorage = gameIDStorage;
        this.memberIDStorage = memberIDStorage;
    }

    public Observable<Game> create(String gameName, String password) {
        return gamesApiService
                .create(new CreateGameDto(gameName, password))
                .doOnNext(result -> {
                    this.gameIDStorage.setId(result._id());
                    this.memberIDStorage.setId(result.owner());
                });
    }

    public Observable<List<Game>> findAllGames() {
        return this.gamesApiService.findAll();
    }

    public Observable<Game> findOneGame(String id) {
        return this.gamesApiService.findOne(id);
    }

    public Observable<Game> updateGame(String id, String name, String password, String owner) {
        return this.gamesApiService.patch(id, new UpdateGameDto(name, owner, password));
    }

    public Observable<Game> deleteGame(String id) {
        return this.gamesApiService.delete(id);
    }
}
