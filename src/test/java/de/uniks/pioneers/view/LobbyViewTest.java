package de.uniks.pioneers.view;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.controller.LobbyController;
import de.uniks.pioneers.controller.LoginController;
import de.uniks.pioneers.controller.RulesScreenController;
import de.uniks.pioneers.service.GameService;
import de.uniks.pioneers.service.GroupService;
import de.uniks.pioneers.service.MessageService;
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
    MessageService messageService;

    @Mock
    EventListener eventListener;

    @InjectMocks
    LobbyController lobbyController;

    private Stage stage;
    private App app;

    @Override
    public void start(Stage stage) {
        // start application
        //init calls need to be mocked here
        when(userService.findAllUsers()).thenReturn(Observable.empty());
        when(gameService.findAllGames()).thenReturn(Observable.empty());
        when(eventListener.listen(any(),any())).thenReturn(Observable.empty());
        when(groupService.getAll()).thenReturn(Observable.empty());
        this.stage = stage;
        this.app = new App(lobbyController);
        this.app.start(stage);
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

        Assertions.assertThat(welcomeLabel.getText()).isEqualTo("Nice to see you again, username!");

        Assertions.assertThat(chatMessage.getText()).isEqualTo("");

        clickOn(chatMessage);
        write("test");
        Assertions.assertThat(chatMessage.getText()).isEqualTo("test");
    }
}
