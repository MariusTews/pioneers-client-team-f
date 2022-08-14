package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.service.*;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class WinnerControllerTest extends ApplicationTest {

    @Spy
    GameStorage gameStorage;

    @Spy
    IDStorage idStorage;

    @Mock
    GameService gameService;

    @Mock
    AchievementsService achievementsService;

    @Mock
    UserService userService;

    @Spy
    Provider<LobbyController> lobbyController;

    @Spy
    App app;

    List<String> pointsAndValues;

    final HashMap<String, List<String>> userNamePAndV = new HashMap<>();

    @ExtendWith(MockitoExtension.class)
    public void start(Stage stage) {
        pointsAndValues = new ArrayList<>();
        pointsAndValues.add("#00000F");
        pointsAndValues.add("10");
        userNamePAndV.put("Tina", pointsAndValues);
        pointsAndValues = new ArrayList<>();
        pointsAndValues.add("#0000DF");
        pointsAndValues.add("6");
        userNamePAndV.put("Syd", pointsAndValues);

        WinnerController winnerController = new WinnerController(userNamePAndV, null, gameStorage
                , idStorage, userService, achievementsService, gameService, app, lobbyController);
        winnerController.render();
    }

    @Test
    public void testParameters() {
        Label winnerTitle = lookup("#winnerTitle").query();
        Assertions.assertThat(winnerTitle.getText()).isEqualTo("Winner");

        Label loserTitle = lookup("#loserTitle").query();
        Assertions.assertThat(loserTitle.getText()).isEqualTo("Loser");

        Assertions.assertThat(!userNamePAndV.isEmpty()).isTrue();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        lobbyController = null;
        userNamePAndV.clear();
        pointsAndValues = null;
        gameStorage = null;
        idStorage = null;
        gameService = null;
        app = null;
    }

}
