package de.uniks.pioneers.view;

import de.uniks.pioneers.*;

import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.controller.LobbyController;
import de.uniks.pioneers.controller.LoginController;
import de.uniks.pioneers.controller.RulesScreenController;
import de.uniks.pioneers.service.GameService;
import de.uniks.pioneers.service.GroupService;
import de.uniks.pioneers.service.UserService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class LobbyViewTest extends ApplicationTest {
    @Mock
    RulesScreenController rulesScreenController;

    @Mock
    LoginController loginController;

    @Mock
    UserService userService;

    @Mock
    GameService gameService;

    @Mock
    GroupService groupService;

    @Mock
    EventListener eventListener;

    @InjectMocks
    LobbyController lobbyController;

    @Override
    public void start(Stage stage) throws Exception {
        when(gameService.findAllGames()).thenReturn(Observable.empty());
        when(userService.findAllUsers()).thenReturn(Observable.empty());
        when(groupService.getAll()).thenReturn(Observable.empty());

        when(eventListener.listen(any(),any())).thenReturn(Observable.empty());

        final App app = new App(null);
        MainComponent testComponent = DaggerTestComponent.builder().mainapp(app).build();
        app.start(stage);
        app.show(lobbyController);
    }

    @Test
    public void testViewParameters() {
        Button rules = lookup("#rulesButton").query();
        Button logout = lookup("#logoutButton").query();
        Button editUser = lookup("#editUserButton").query();
        Button createGame = lookup("#createGameButton").query();
        Button send = lookup("#sendButton").query();

        Label welcomeLabel = lookup("#userWelcomeLabel").query();

        TextField chatMessage = lookup("#chatMessageField").query();

        Assertions.assertThat(rules.getText()).isEqualTo("Rules");
        Assertions.assertThat(logout.getText()).isEqualTo("Logout");
        Assertions.assertThat(editUser.getText()).isEqualTo("Edit User");
        Assertions.assertThat(createGame.getText()).isEqualTo("Create Game");
        Assertions.assertThat(send.getText()).isEqualTo("send");

        Assertions.assertThat(welcomeLabel.getText()).isEqualTo("Nice to see you again, ");

        Assertions.assertThat(chatMessage.getText()).isEqualTo("");

        clickOn(chatMessage);
        write("test");
        Assertions.assertThat(chatMessage.getText()).isEqualTo("test");
    }
}
