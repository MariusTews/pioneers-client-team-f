package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.service.GameService;
import de.uniks.pioneers.service.MemberService;
import de.uniks.pioneers.service.MessageService;
import de.uniks.pioneers.service.UserService;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

@ExtendWith(MockitoExtension.class)
public class GameLobbyControllerTest extends ApplicationTest {
    @Mock
    MemberService memberService;

    @Mock
    UserService userService;

    @Mock
    MessageService messageService;

    @Mock
    GameService gameService;

    @InjectMocks
    GameLobbyController gameLobbyController;

    @Override
    public void start(Stage stage) throws Exception {

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
