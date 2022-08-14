package de.uniks.pioneers.computation;

import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

public class SpectatorRenderInGameTest {

    @InjectMocks
    SpectatorRenderInGame spectatorRenderInGame;

    @Test
    void renderSingleSpectatorTest() {

        spectatorRenderInGame = new SpectatorRenderInGame();

        ObservableList<User> allUsers = FXCollections.observableArrayList();

        User user = new User("2022-11-30T18:35:24.00Z", "1:00", "7", "Bob", null, null, null);
        allUsers.add(user);

        spectatorRenderInGame.renderSingleSpectator(allUsers, "7", new Label());

        Assertions.assertEquals(allUsers.size(), 1);

    }

    @Test
    void checkMemberTest() {
        ObservableList<User> allUsers = FXCollections.observableArrayList();

        User user = new User("2022-11-30T18:35:24.00Z", "1:00", "7", "Bob", null, null, null);
        allUsers.add(user);
        ObservableList<Member> members = FXCollections.observableArrayList();
        Member member = new Member("2022-11-30T18:35:24.00Z", "1:00", "id", "7", true, "#ffa510", false);
        members.add(member);

        Pane spectatorPane = new Pane();
        Label spectatorLabel = new Label();
        spectatorLabel.setText("Spectator");

        spectatorRenderInGame = new SpectatorRenderInGame();
        spectatorRenderInGame.checkMember(members, spectatorPane, allUsers, spectatorLabel, new ImageView());

        Assertions.assertEquals(allUsers.size(), 1);
        Assertions.assertEquals(members.size(), 1);

    }
}
