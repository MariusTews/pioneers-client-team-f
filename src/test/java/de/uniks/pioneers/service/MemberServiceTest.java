package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.CreateMemberDto;
import de.uniks.pioneers.dto.UpdateMemberDto;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.rest.GameMembersApiService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
    @Spy
    GameIDStorage gameIDStorage;

    @Spy
    MemberIDStorage memberIDStorage;

    @Mock
    GameMembersApiService gameMembersApiService;

    @InjectMocks
    MemberService memberService;

    @Test
    void join() {
        when(gameMembersApiService.create(any(), any())).thenReturn(Observable.just(new Member("0:30",
                                                "15:50", "01", "02", false, Color.BLACK)));
        Member member = memberService.join("testUser", "testGame","testPassword").blockingFirst();
        assertEquals("Member[createdAt=0:30, updatedAt=15:50, gameId=01, userId=02, ready=false, color=0x000000ff]", member.toString());
        assertEquals("01",gameIDStorage.getId());
        assertEquals("02", memberIDStorage.getId());
        verify(gameMembersApiService).create("testGame", new CreateMemberDto(false, Color.BLACK,"testPassword"));
    }

    @Test
    void leave() {
        when(gameMembersApiService.delete(any(),any())).thenReturn(Observable.just(new Member("0:30", "15:50",
                                                                            "01", "02", false, Color.BLACK)));
        Member member = memberService.leave("testGame","testUser").blockingFirst();
        assertEquals("Member[createdAt=0:30, updatedAt=15:50, gameId=01, userId=02, ready=false, color=0x000000ff]", member.toString());
        verify(gameMembersApiService).delete("testGame", "testUser");
    }

    @Test
    void findOne() {
        when(gameMembersApiService.findOne(any(),any())).thenReturn(Observable.just(new Member("0:30",
                                                "15:50", "01", "02", false, Color.BLACK)));
        Member member = memberService.findOne("testGame","testUser").blockingFirst();
        assertEquals("Member[createdAt=0:30, updatedAt=15:50, gameId=01, userId=02, ready=false, color=0x000000ff]", member.toString());
        verify(gameMembersApiService).findOne("testGame", "testUser");
    }

    @Test
    void getAllGameMembers() {
        when(gameMembersApiService.findAll(any())).thenReturn(Observable.just(new ArrayList<>(Collections
                .singleton(new Member("0:30", "15:50", "01", "02", false, Color.BLACK)))));
        ArrayList<Member> member = (ArrayList<Member>) memberService.getAllGameMembers("testGame").blockingFirst();
        assertEquals("[Member[createdAt=0:30, updatedAt=15:50, gameId=01, userId=02, ready=false, color=0x000000ff]]", member.toString());
        verify(gameMembersApiService).findAll("testGame");
    }

    @Test
    void statusUpdate() {
        when(gameMembersApiService.patch(any(),any(),any())).thenReturn(Observable.just(new Member("0:30",
                                                "15:50", "01", "02", false, Color.BLACK)));
        Member member = memberService.statusUpdate("testGame", "testUser", false, Color.BLACK).blockingFirst();
        assertEquals("Member[createdAt=0:30, updatedAt=15:50, gameId=01, userId=02, ready=false, color=0x000000ff]",member.toString());
        verify(gameMembersApiService).patch("testGame", "testUser", new UpdateMemberDto(false,Color.BLACK));
    }

}
