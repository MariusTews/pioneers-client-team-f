package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.CreateGameDto;
import de.uniks.pioneers.dto.UpdateGameDto;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.GameSettings;
import de.uniks.pioneers.rest.GamesApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class GameService {

    private final GamesApiService gamesApiService;
    private final GameStorage gameStorage;
    private final MemberIDStorage memberIDStorage;

    @Inject
    public GameService(GamesApiService gamesApiService, GameStorage gameStorage, MemberIDStorage memberIDStorage) {
        this.gamesApiService = gamesApiService;
        this.gameStorage = gameStorage;
        this.memberIDStorage = memberIDStorage;
    }

    public Observable<Game> create(String gameName, String password, int mapSize, int victoryPoints) {
        return gamesApiService
                .create(new CreateGameDto(gameName, false, new GameSettings(mapSize, victoryPoints), password))
                .doOnNext(result -> {
                    this.gameStorage.setId(result._id());

                    this.memberIDStorage.setId(result.owner());
                });
    }

    public Observable<List<Game>> findAllGames() {
        return this.gamesApiService.findAll();
    }

    public Observable<Game> findOneGame(String id) {
        return this.gamesApiService.findOne(id);
    }

    public Observable<Game> updateGame(String id, String name, String password, String owner, boolean started, int mapSize, int victoryPoints) {
        return this.gamesApiService.patch(id, new UpdateGameDto(name, owner, started, new GameSettings(mapSize, victoryPoints), password));
    }

    public Observable<Game> deleteGame(String id) {
        return this.gamesApiService.delete(id);
    }
}
