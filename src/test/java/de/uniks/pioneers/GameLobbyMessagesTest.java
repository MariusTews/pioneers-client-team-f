package de.uniks.pioneers;

import de.uniks.pioneers.controller.GameLobbyController;
import de.uniks.pioneers.service.GameIDStorage;
import de.uniks.pioneers.service.MemberIDStorage;
import javafx.stage.Stage;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

@ExtendWith(MockitoExtension.class)
public class GameLobbyMessagesTest extends ApplicationTest {
    @Spy
    GameIDStorage gameIDStorage;

    @Spy
    MemberIDStorage memberIDStorage;

    @InjectMocks
    GameLobbyController gameLobbyController;

    private Stage stage;
}
