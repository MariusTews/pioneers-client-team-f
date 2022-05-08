package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.CreateMemberDto;
import de.uniks.pioneers.dto.UpdateMemberDto;
import de.uniks.pioneers.model.Member;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

public interface GameMembersApiService {

    @GET("games/{gameId}/members")
    Observable<List<Member>> findAll(@Path("gameId") String gameId);

    @POST("games/{gameId}/members")
    Observable<Member> create(@Path("gameId") String gameId,
                              @Body CreateMemberDto dto);

    @GET("games/{gameId}/members/{userId}")
    Observable<Member> findOne(@Path("gameId") String gameId,
                               @Path("userId") String userId);

    @PATCH("games/{gameId}/members/{userId}")
    Observable<Member> patch(@Path("gameId") String gameId,
                             @Path("userId") String userId,
                             @Body UpdateMemberDto dto);

    @DELETE("games/{gameId}/members/{userId}")
    Observable<Member> delete(@Path("gameId") String gameId,
                              @Path("userId") String userId);
}
