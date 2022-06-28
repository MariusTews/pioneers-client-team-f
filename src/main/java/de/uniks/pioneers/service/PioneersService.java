package de.uniks.pioneers.service;

import de.uniks.pioneers.Constants;
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

	public Observable<Move> move(String gameId, String action, Number x, Number y, Number z, Number side, String type) {
		if (x == null && y == null && z == null && side == null && type == null) {
			return this.pioneersApiService.create(gameId, new CreateMoveDto(action, null, null, null, null));
		} else {
			return this.pioneersApiService.create(gameId, new CreateMoveDto(action, null, null, null, new CreateBuildingDto(x, y, z, side, type)));
		}
	}

	public Observable<Move> tradeBank(String gameId, HashMap<String, Integer> resources) {
		return this.pioneersApiService.create(gameId, new CreateMoveDto("build", null, resources, Constants.BANK_ID, null));
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

	public Observable<Move> rob(String gameId, String oppId) {
		return pioneersApiService.create(gameId, new CreateMoveDto("rob", new RobDto(-1, 0, 1, oppId), null, null, null));
	}
}
