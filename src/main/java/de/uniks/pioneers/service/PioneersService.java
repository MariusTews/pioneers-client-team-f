package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.CreateBuildingDto;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.rest.PioneersApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
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
}
