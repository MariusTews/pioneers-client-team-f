package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.CreateGameDto;
import de.uniks.pioneers.dto.UpdateGameDto;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.GameSettings;
import de.uniks.pioneers.rest.GamesApiService;
import de.uniks.pioneers.util.JsonUtil;
import de.uniks.pioneers.util.ResourceManager;
import io.reactivex.rxjava3.core.Observable;
import kong.unirest.json.JSONObject;

import javax.inject.Inject;
import java.util.List;

import static de.uniks.pioneers.Constants.JSON_NAME;
import static de.uniks.pioneers.Constants.JSON_TOKEN;

@SuppressWarnings("ClassCanBeRecord")
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

    public Observable<Game> create(String gameName, String password, int mapSize, int victoryPoints, String mapTemplate, boolean roll7, int startingResources) {
        return gamesApiService
                .create(new CreateGameDto(gameName, false, new GameSettings(mapSize, victoryPoints, mapTemplate, roll7, startingResources), password))
                .doOnNext(result -> {
                    this.gameStorage.setId(result._id());
                    this.memberIDStorage.setId(result.owner());
                    //save game id in config file to check for rejoin ability even when app was closed before
                    ResourceManager.saveConfig(JsonUtil.updateConfigWithGameId(result._id()));
                });
    }

    public Observable<List<Game>> findAllGames() {
        return this.gamesApiService.findAll();
    }

    public Observable<Game> findOneGame(String id) {
        return this.gamesApiService.findOne(id);
    }

    public Observable<Game> updateGame(String id, String name, String password, String owner, boolean started, int mapSize, int victoryPoints, String mapTemplate, boolean roll7, int startingResources) {
        return this.gamesApiService.patch(id, new UpdateGameDto(name, owner, started, new GameSettings(mapSize, victoryPoints, mapTemplate, roll7, startingResources), password));
    }

    public Observable<Game> deleteGame(String id) {
        return this.gamesApiService.delete(id);
    }
}
