package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.service.GameStorage;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.PioneersService;
import de.uniks.pioneers.service.UserService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserSubViewTest extends ApplicationTest {
    @Spy
    GameStorage gameStorage;
    @Mock
    PioneersService pioneersService;
    @Spy
    IDStorage idStorage;

    @Mock
    GameFieldSubController gameFieldSubController;
    @Mock
    UserService userService;

    final HashMap<String, Integer> hm = new HashMap<>() {{
        put("wool", 2);
        put("grain", 3);
        put("ore", 2);
        put("lumber", 2);
        put("brick", 6);
    }};

    @InjectMocks
    UserSubView userSubView = new UserSubView(idStorage, gameStorage, userService, new Player("id", "2",
            "#000000", true, 2, hm, new HashMap<>(), 2, 2, false, false, null, null), gameFieldSubController, 10, pioneersService);


    public void start(Stage stage) {
        

        when(idStorage.getID()).thenReturn("2");

        when(userService.findAllUsers()).thenReturn(Observable.just(List.of(
                new User("1234", "12345", "2", "tests", "online", null, new ArrayList<>()))));

        userSubView = new UserSubView(idStorage, gameStorage, userService, new Player("id", "2",
                "#000000", true, 2, hm, new HashMap<>(), 2, 10, false, false, null, null), gameFieldSubController, 10, pioneersService);

        final App app = new App(userSubView);
        app.start(stage);
    }

    @Test
    public void testParameters() {
        Label item1 = lookup("#item1").query();
        Label item2 = lookup("#item2").query();
        Label item3 = lookup("#item3").query();
        Label item4 = lookup("#item4").query();
        Label item5 = lookup("#item5").query();
        Assertions.assertThat(item1.getText()).isEqualTo("2");
        Assertions.assertThat(item2.getText()).isEqualTo("6");
        Assertions.assertThat(item3.getText()).isEqualTo("2");
        Assertions.assertThat(item4.getText()).isEqualTo("2");
        Assertions.assertThat(item5.getText()).isEqualTo("3");

        Label name = lookup("#name").query();
        Assertions.assertThat(name.getText()).isEqualTo("tests (YOU)");
    }

    @Test
    public void onClickTests() {
        Button onBuild = lookup("#sett").query();
        clickOn(onBuild);

        org.junit.jupiter.api.Assertions.assertEquals(onBuild.getText(), "UFO");

        Button onRoad = lookup("#road").query();
        clickOn(onRoad);

        org.junit.jupiter.api.Assertions.assertEquals(onRoad.getText(), "Tube");

        Button onCity = lookup("#city").query();
        clickOn(onCity);

        org.junit.jupiter.api.Assertions.assertEquals(onCity.getText(), "Station");
    }

    @Test
    public void onClickDevTests() {
        when(gameStorage.getId()).thenReturn("id");
        List<String> players = new ArrayList<>();
        players.add("2");
        List<ExpectedMove> expectedMoves = new ArrayList<>();
        expectedMoves.add(new ExpectedMove("build", players));
        when(pioneersService.findOneState("id")).thenReturn(Observable.just(
                new State("12:30", "id", expectedMoves, null)));
        when(pioneersService.findOnePlayer("id", "2")).thenReturn(Observable.empty());
        Button onDev = lookup("#developmentBuyIdButton").query();
        clickOn(onDev);
        org.junit.jupiter.api.Assertions.assertEquals(onDev.getText(), "Dev card");

    }

    @Test
    public void onClickDevGetError() {
        when(gameStorage.getId()).thenReturn("id");
        List<String> players = new ArrayList<>();
        players.add("d");
        List<ExpectedMove> expectedMoves = new ArrayList<>();
        expectedMoves.add(new ExpectedMove("build", players));
        when(pioneersService.findOneState("id")).thenReturn(Observable.just(
                new State("12:30", "id", expectedMoves, null)));
        Button onDev = lookup("#developmentBuyIdButton").query();
        clickOn(onDev);
        org.junit.jupiter.api.Assertions.assertEquals(onDev.getText(), "Dev card");

    }

    @Test
    public void onClickDevGetSecondError() {
        when(gameStorage.getId()).thenReturn("id");
        List<String> players = new ArrayList<>();
        players.add("d");
        List<ExpectedMove> expectedMoves = new ArrayList<>();
        expectedMoves.add(new ExpectedMove("", players));
        when(pioneersService.findOneState("id")).thenReturn(Observable.just(
                new State("12:30", "id", expectedMoves, null)));
        Button onDev = lookup("#developmentBuyIdButton").query();
        clickOn(onDev);
        org.junit.jupiter.api.Assertions.assertEquals(onDev.getText(), "Dev card");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        gameFieldSubController = null;
        hm.clear();
    }
}
