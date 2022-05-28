package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.CreateMemberDto;
import de.uniks.pioneers.dto.UpdateMemberDto;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.rest.GameMembersApiService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.util.List;

public class MemberService {

    private final GameMembersApiService gameMembersApiService;

    // id of game and member needed when joining
    private final GameIDStorage gameIDStorage;
    private final MemberIDStorage memberIDStorage;

    @Inject
    public MemberService(GameMembersApiService gameMembersApiService, GameIDStorage gameIDStorage, MemberIDStorage memberIDStorage) {
        this.gameMembersApiService = gameMembersApiService;
        this.gameIDStorage = gameIDStorage;
        this.memberIDStorage = memberIDStorage;
    }

    public Observable<List<Member>> getAllGameMembers(String gameId) {
        return gameMembersApiService.findAll(gameId);
    }

    public Observable<Member> join(String userId, String gameID, String password) {
        return gameMembersApiService
                .create(gameID, new CreateMemberDto(false, Color.BLACK, password))
                .doOnNext(result -> {
                    this.gameIDStorage.setId(result.gameId());
                    this.memberIDStorage.setId(result.userId());
                });
    }


    public Observable<Member> statusUpdate(String gameId, String userId, boolean status, Color color){
        return gameMembersApiService.patch(gameId,userId,new UpdateMemberDto(status, color));
    }

    public Observable<Member> findOne(String gameId, String userId){
        return gameMembersApiService.findOne(gameId, userId);
    }


    public Observable<Member> leave(String gameId, String userID) {
        return gameMembersApiService
                .delete(gameId, userID);
    }

}
