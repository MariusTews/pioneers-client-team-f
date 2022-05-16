package de.uniks.pioneers.service;

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
}
