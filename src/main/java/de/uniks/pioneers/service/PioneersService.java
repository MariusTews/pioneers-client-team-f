package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.CreateBuildingDto;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.dto.RobDto;
import de.uniks.pioneers.dto.UpdatePlayerDto;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.rest.PioneersApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;

import static de.uniks.pioneers.Constants.*;

@SuppressWarnings("ClassCanBeRecord")
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
                    resources, null, null, null));
        } else if (action.equals(ROB_ACTION)) {
            return this.pioneersApiService.create(gameId, new CreateMoveDto(action, new RobDto(x, y, z, target), null, null, null, null));
        } else if (action.equals("build") && type!=null && type.equals("new")) {
            return this.pioneersApiService.create(gameId, new CreateMoveDto(action, null, null, null, type, null));
        } else if (x == null && y == null && z == null && side == null && type == null) {
            return this.pioneersApiService.create(gameId, new CreateMoveDto(action, null, null, null, null, null));
        } else {
            return this.pioneersApiService.create(gameId, new CreateMoveDto(action, null, null, null, null, new CreateBuildingDto(x, y, z, side, type)));
        }
    }

    public Observable<Move> tradeBank(String gameId, HashMap<String, Integer> resources) {
		return this.pioneersApiService.create(gameId, new CreateMoveDto("build", null, resources, BANK_ID, null, null));
	}

    public Observable<Move> tradePlayer(String gameId, String action, String partner, HashMap<String, Integer> resources) {
        return this.pioneersApiService.create(gameId, new CreateMoveDto(action, null, resources, partner, null, null));
    }

	public Observable<Player> updatePlayer(String gameId, String userId, boolean active) {
		return this.pioneersApiService.updatePlayer(gameId, userId, new UpdatePlayerDto(active));
	}

    // methode added for later use
    @SuppressWarnings("unused")
    public Observable<List<Move>> findAllMoves(String gameId) {
        return this.pioneersApiService.findAllMoves(gameId);
    }

    // methode added for later use
    @SuppressWarnings("unused")
    public Observable<Move> findOneMove(String gameId, String moveId) {
        return this.pioneersApiService.findOneMove(gameId, moveId);
    }
}
