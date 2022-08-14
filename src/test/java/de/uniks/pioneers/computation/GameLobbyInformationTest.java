package de.uniks.pioneers.computation;

import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.MemberService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

public class GameLobbyInformationTest {
    @Spy
    IDStorage idStorage;

    @Mock
    MemberService memberService;

    @InjectMocks
    GameLobbyInformation gameLobbyInformation;

    @Test
    void giveYourselfTest() {
        idStorage = new IDStorage();
        idStorage.setID("id");
        memberService = new MemberService(null, null, null);

        ObservableList<Member> members = FXCollections.observableArrayList();
        Member member = new Member("2022-11-30T18:35:24.00Z", "1:00", "id", "7", true, "#ffa510", false);
        members.add(member);

        gameLobbyInformation = new GameLobbyInformation();
        gameLobbyInformation.giveYourSelfColour(members, memberService, idStorage);

        Assertions.assertEquals(members.size(), 1);
    }

    @Test
    void renderSpectatorMemberTest() {

        Member member = new Member("2022-11-30T18:35:24.00Z", "1:00", "id", "7", true, "#ffa510", true);
        ObservableList<User> allUsers = FXCollections.observableArrayList();

        User user = new User("2022-11-30T18:35:24.00Z", "1:00", "7", "Bob", null, null, null);
        allUsers.add(user);

        gameLobbyInformation = new GameLobbyInformation();
        gameLobbyInformation.renderSpectatorMember(member, allUsers);

        Assertions.assertEquals(allUsers.size(), 1);
    }

}
