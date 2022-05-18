package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.service.*;
import io.reactivex.rxjava3.core.Observable;
import javafx.stage.Stage;
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

    @Override
    public void start(Stage stage) throws Exception {
        // init calls
        when(memberService.getAllGameMembers(any())).thenReturn(Observable.empty());
        when(userService.findAllUsers()).thenReturn(Observable.empty());
        when(messageService.getAllMessages(any(), any())).thenReturn(Observable.empty());
        when(eventListener.listen(any(), any())).thenReturn(Observable.empty());
        new App(gameLobbyController).start(stage);
    }

    @Test
    public void memberListTest() {

    }

    @Test
    public void sendMessageTest() {

    }

    @Test
    public void deleteMessageTest() {

    }

    @Test
    public void failedDeleteMessageTest() {

    }

    @Test
    public void memberLeavesGameTest() {

    }
}
