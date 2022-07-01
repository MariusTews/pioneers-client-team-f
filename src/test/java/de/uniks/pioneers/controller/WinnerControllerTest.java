package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.service.GameService;
import de.uniks.pioneers.service.GameStorage;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.PioneersService;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;
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
    PioneersService pioneersService;

    @Mock
    GameService gameService;

    Window  window;

    Provider<LobbyController> lobbyController;

    App app;

    List<String> pointsAndValues;

    HashMap<String, List<String>> userNamePAndV = new HashMap<>();



    @ExtendWith(MockitoExtension.class)
    public void start(Stage stage){
        pointsAndValues = new ArrayList<>();
        pointsAndValues.add("#00000F");
        pointsAndValues.add("10");
        userNamePAndV.put("Tina",pointsAndValues);
        pointsAndValues = new ArrayList<>();
        pointsAndValues.add("#0000DF");
        pointsAndValues.add("6");
        userNamePAndV.put("Syd",pointsAndValues);

        WinnerController winnerController = new WinnerController(userNamePAndV,null,pioneersService,gameStorage
                ,idStorage, gameService, app, lobbyController);
        winnerController.render();

    }

    @Test
    public void testParameters(){
        Label winnerTitel = lookup("#winnerTitel").query();
        Assertions.assertThat(winnerTitel.getText().equals("Winner"));

        Label loserTitel = lookup("#loserTitel").query();
        Assertions.assertThat(loserTitel.getText().equals("Loser"));

        Assertions.assertThat(!userNamePAndV.isEmpty());
    }

}
