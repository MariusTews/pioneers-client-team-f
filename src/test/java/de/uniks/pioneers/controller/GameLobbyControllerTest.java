package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.Message;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.*;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.TextInputControlMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameLobbyControllerTest extends ApplicationTest {
    @Spy
    GameIDStorage gameIDStorage;
    @Spy
    MemberIDStorage memberIDStorage;

    @Mock
    MemberService memberService;
    @Mock
    UserService userService;
    @Mock
    MessageService messageService;
    @Mock
    GameService gameService;
    @Mock
    EventListener eventListener;

    @InjectMocks
    GameLobbyController gameLobbyController;

    private Stage stage;
    private App app;

    @Override
    public void start(Stage stage) {
        Member m1 = new Member("0", "0", "g1", "u1", false);
        Member m2 = new Member("1", "1", "g1", "u2", false);

        User u1 = new User("u1", "a", "on", null);
        User u2 = new User("u2", "b", "on", null);

        Message x1 = new Message("0", "0", "me1", "u1", "test1");
        Message x2 = new Message("1", "1", "me2", "u2", "test2");

        Game g = new Game("0", "0", "g1", "g", "u1",  2);

        this.gameIDStorage.setId("g1");

        when(memberService.getAllGameMembers(any())).thenReturn(Observable.just(m1, m2).buffer(2));
        when(userService.findAllUsers()).thenReturn(Observable.just(u1, u2).buffer(2));
        when(userService.findOne(any())).thenReturn(Observable.empty());
        when(eventListener.listen(any(), any())).thenReturn(Observable.empty());
        when(gameService.findOneGame(any())).thenReturn(Observable.just(g));
        when(messageService.send(any(), any(), any())).thenReturn(Observable.just(x1));
        when(messageService.getAllMessages(any(), any())).thenReturn(Observable.just(x1, x2).buffer(2));

        this.stage = stage;
        this.app = new App(gameLobbyController);
        this.app.start(stage);
    }

    @Test
    public void sendMessageTest() {
        // jump to messageField
        write("\ttest1");
        FxAssert.verifyThat("#idMessageField", TextInputControlMatchers.hasText("test1"));

        write("\t");
        type(KeyCode.SPACE);

        VBox box = lookup("#idMessageView").query();
        HBox messages = (HBox) box.getChildren().get(0);
        Label label = (Label) messages.getChildren().get(1);

        Assertions.assertEquals("a: test1", label.getText());

        verify(messageService).send("games", "g1", "test1");
        verify(memberService).getAllGameMembers("g1");
        verify(userService).findAllUsers();
        verify(userService).findOne("u1");
        verify(eventListener).listen("games.g1.messages.*.*", Message.class);
        verify(messageService).getAllMessages("games", "g1");
    }
}
