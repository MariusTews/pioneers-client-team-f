package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.*;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.rest.PioneersApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;

import static de.uniks.pioneers.Constants.*;

public class PioneersService {

    private final PioneersApiService pioneersApiService;

    @Inject
    public PioneersService(PioneersApiService pioneersApiService) {
        this.pioneersApiService = pioneersApiService;
    }

    public Observable<Map> findAllTiles(String gameId) {
        return this.pioneersApiService.findAllTiles(gameId);
    }

    public Observable<List<Player>> findAllPlayers(String gameId) {
        return this.pioneersApiService.findAllPlayers(gameId);
    }

    public Observable<Player> findOnePlayer(String gameId, String userId) {
        return this.pioneersApiService.findOnePlayer(gameId, userId);
    }

    public Observable<State> findOneState(String gameId) {
        return this.pioneersApiService.findOneState(gameId);
    }

    public Observable<List<Building>> findAllBuildings(String gameId) {
        return this.pioneersApiService.findAllBuildings(gameId);
    }

    public Observable<Building> findOneBuilding(String gameId, String buildingId) {
        return this.pioneersApiService.findOneBuilding(gameId, buildingId);
    }

    public Observable<Move> move(String gameId, String action, Number x, Number y, Number z, Number side,
                                 String type, String target, HashMap<String, Integer> resources) {
        if (action.equals(DROP_ACTION) && resources != null) {
            return this.pioneersApiService.create(gameId, new CreateMoveDto(action, null,
                    new ResourcesDto(resources.get(VENUS_GRAIN), resources.get(MARS_BAR), resources.get(MOON_ROCK),
                            resources.get(EARTH_CACTUS), resources.get(NEPTUNE_CRYSTAL)), null, null));
        } else if (action.equals(ROB_ACTION)) {
            return this.pioneersApiService.create(gameId, new CreateMoveDto(action, new RobDto(x, y, z, target), null, null, null));
        } else if (x == null && y == null && z == null && side == null && type == null) {
            return this.pioneersApiService.create(gameId, new CreateMoveDto(action, null, null, null, null));
        } else {
            return this.pioneersApiService.create(gameId, new CreateMoveDto(action, null, null, null, new CreateBuildingDto(x, y, z, side, type)));
        }
    }

    public Observable<Player> updatePlayer(String gameId, String userId, boolean active) {
        return this.pioneersApiService.updatePlayer(gameId, userId, new UpdatePlayerDto(active));
    }

    public Observable<List<Move>> findAllMoves(String gameId) {
        return this.pioneersApiService.findAllMoves(gameId);
    }

    public Observable<Move> findOneMove(String gameId, String moveId) {
        return this.pioneersApiService.findOneMove(gameId, moveId);
    }
}
