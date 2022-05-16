package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.CreateMemberDto;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.rest.GameMembersApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class MemberService {

    private GameMembersApiService gameMembersApiService;

    @Inject
    public MemberService(GameMembersApiService gameMembersApiService) {

        this.gameMembersApiService = gameMembersApiService;
    }

    public Observable<Member> join(String gameID, String password) {
        return gameMembersApiService
                .create(gameID, new CreateMemberDto(false,  password));

    }
}
