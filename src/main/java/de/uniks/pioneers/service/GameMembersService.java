package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.CreateMemberDto;
import de.uniks.pioneers.dto.UpdateMemberDto;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.rest.GameMembersApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class GameMembersService {

    private final GameMembersApiService gameMembersApiService;

    @Inject
    public GameMembersService(GameMembersApiService gameMembersApiService) {
        this.gameMembersApiService = gameMembersApiService;
    }

    public Observable<List<Member>> getAllGameMembers(String gameId) {
        return gameMembersApiService.findAll(gameId);
    }

    public Observable<Member> join(String gameId, String password) {
        return gameMembersApiService.create(gameId, new CreateMemberDto(false, password));
    }

    public Observable<Member> findOne(String gameId, String userId) {
        return gameMembersApiService.findOne(gameId, userId);
    }

    public Observable<Member> patch(String gameId, String userId, Boolean ready) {
        return gameMembersApiService.patch(gameId, userId, new UpdateMemberDto(ready));
    }

    public Observable<Member> delete(String gameId, String userId) {
        return gameMembersApiService.delete(gameId, userId);
    }
}
