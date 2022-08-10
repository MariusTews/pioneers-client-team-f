package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.CreateMemberDto;
import de.uniks.pioneers.dto.UpdateMemberDto;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.rest.GameMembersApiService;
import de.uniks.pioneers.util.JsonUtil;
import de.uniks.pioneers.util.ResourceManager;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
public class MemberService {

	private final GameMembersApiService gameMembersApiService;

	// id of game and member needed when joining
	private final GameStorage gameStorage;
	private final MemberIDStorage memberIDStorage;

	@Inject
	public MemberService(GameMembersApiService gameMembersApiService, GameStorage gameStorage, MemberIDStorage memberIDStorage) {
		this.gameMembersApiService = gameMembersApiService;
		this.gameStorage = gameStorage;
		this.memberIDStorage = memberIDStorage;
	}

	public Observable<List<Member>> getAllGameMembers(String gameId) {
		return gameMembersApiService.findAll(gameId);
	}

	public Observable<Member> join(String gameID, String password) {
		return gameMembersApiService
				.create(gameID, new CreateMemberDto(false, "#000000", false, password))
				.doOnNext(result -> {
					this.gameStorage.setId(result.gameId());
					this.memberIDStorage.setId(result.userId());
					//save game id in config file to check for rejoin ability even when app was closed before
					ResourceManager.saveConfig(JsonUtil.updateConfigWithGameId(result.gameId()));
				});
	}


	public Observable<Member> statusUpdate(String gameId, String userId, boolean status, String color, boolean spectator) {
		return gameMembersApiService.patch(gameId, userId, new UpdateMemberDto(status, color, spectator));
	}

	public Observable<Member> findOne(String gameId, String userId) {
		return gameMembersApiService.findOne(gameId, userId);
	}


	public Observable<Member> leave(String gameId, String userID) {
		return gameMembersApiService
				.delete(gameId, userID);
	}
}
