package de.uniks.pioneers.view;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.controller.GameLobbyController;
import de.uniks.pioneers.controller.LobbyController;
import de.uniks.pioneers.service.*;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameLobbyViewTest extends ApplicationTest {
    @Mock
    MemberService memberService;

    @Mock
    UserService userService;

    @Mock
    MessageService messageService;

    @Mock
    GameService gameService;

    @Mock
    LobbyController lobbyController;

    @Mock
    EventListener eventListener;

    @Spy
    GameIDStorage gameIDStorage;

    @Spy
    MemberIDStorage memberIDStorage;

    @InjectMocks
    GameLobbyController gameLobbyController;

    private Stage stage;
    private App app;

    @Override
    public void start(Stage stage) {
        // empty init
        when(memberService.getAllGameMembers(any())).thenReturn(Observable.empty());
        when(userService.findAllUsers()).thenReturn(Observable.empty());
        when(messageService.getAllMessages(any(), any())).thenReturn(Observable.empty());
        when(eventListener.listen(any(), any())).thenReturn(Observable.empty());
        when(gameService.findOneGame(any())).thenReturn(Observable.empty());

        // start application
        this.stage = stage;
        this.app = new App(gameLobbyController);
        this.app.start(stage);
    }

    @Test
    public void testGameLobbyUIElement() {
        // buttons
        Button leaveButton = lookup("#idLeaveButton").query();
        Button sendButton = lookup("#idSendButton").query();
        Button readyButton = lookup("#idReadyButton").query();
        Button startGameButton = lookup("#idStartGameButton").query();

        // label
        Label titleLabel = lookup("#idTitleLabel").query();

        // textfield
        TextField messageField = lookup("#idMessageField").query();

        // assertions buttons
        Assertions.assertEquals(leaveButton.getText(), "Leave");
        Assertions.assertEquals(sendButton.getText(), "Send");
        Assertions.assertEquals(readyButton.getText(), "Ready");
        Assertions.assertEquals(startGameButton.getText(), "Start Game");

        // assertion title
        Assertions.assertEquals(titleLabel.getText(), "");

        // assertion message field
        clickOn(messageField);
        write("something");
        Assertions.assertEquals(messageField.getText(), "something");

    }
}
