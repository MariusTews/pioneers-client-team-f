package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.service.GameStorage;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.PioneersService;
import de.uniks.pioneers.Websocket.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static de.uniks.pioneers.Constants.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TradingSubControllerTest extends ApplicationTest {

    final HashMap<String, Integer> resources = new HashMap<>() {{
        put(VENUS_GRAIN, 3);
        put(MOON_ROCK, 5);
        put(MARS_BAR, 3);
        put(NEPTUNE_CRYSTAL, 4);
        put(EARTH_CACTUS, 4);
    }};

    @Mock
    PioneersService pioneersService;

    @Mock
    EventListener eventListener;

    @Spy
    GameStorage gameStorage;

    @Spy
    IDStorage idStorage;

    @InjectMocks
    TradingSubController tradingSubController;

    @ExtendWith(MockitoExtension.class)
    public void start(Stage stage) {
        List<Tile> tiles = new ArrayList<>();
        tiles.add(new Tile(1,1,1, "grain", 1));

        List<Harbor> harbors = new ArrayList<>();
        harbors.add(new Harbor(1, 0, -1, "lumber", 1));
        harbors.add(new Harbor(1, -1, 0, "wool", 3));
        harbors.add(new Harbor(0, -1, 1, "grain", 5));
        harbors.add(new Harbor(-1, 0, 1, null, 7));
        harbors.add(new Harbor(-1, 1, 0, "ore", 9));
        harbors.add(new Harbor(0, 1, -1, "brick", 11));



        when(pioneersService.findAllPlayers(any())).thenReturn(Observable.just(new Player("g1", "u1", "b", true, 1, resources, null, 2, 3, null, null)).buffer(1));
        when(pioneersService.findAllTiles(any())).thenReturn(Observable.just(new Map("g1", tiles, harbors)));
        when(eventListener.listen(any(), any())).thenReturn(Observable.empty());
        when(gameStorage.getId()).thenReturn("g1");
        when(idStorage.getID()).thenReturn("u1");

        // start application
        App app = new App(tradingSubController);
        app.start(stage);
    }

    @Test
    public void testButtons() {
        Button giveCactusPlusButton = lookup("#giveCactusPlusButton").queryButton();
        Button giveMarsPlusButton = lookup("#giveMarsPlusButton").queryButton();
        Button giveMoonPlusButton = lookup("#giveMoonPlusButton").queryButton();
        Button giveNeptunPlusButton = lookup("#giveNeptunPlusButton").queryButton();
        Button giveVenusPlusButton = lookup("#giveVenusPlusButton").queryButton();

        Button giveCactusMinusButton = lookup("#giveCactusMinusButton").queryButton();
        Button giveMarsMinusButton = lookup("#giveMarsMinusButton").queryButton();
        Button giveMoonMinusButton = lookup("#giveMoonMinusButton").queryButton();
        Button giveNeptunMinusButton = lookup("#giveNeptunMinusButton").queryButton();
        Button giveVenusMinusButton = lookup("#giveVenusMinusButton").queryButton();

        Button receiveCactusPlusButton = lookup("#receiveCactusPlusButton").queryButton();
        Button receiveMarsPlusButton = lookup("#receiveMarsPlusButton").queryButton();
        Button receiveMoonPlusButton = lookup("#receiveMoonPlusButton").queryButton();
        Button receiveNeptunPlusButton = lookup("#receiveNeptunPlusButton").queryButton();
        Button receiveVenusPlusButton = lookup("#receiveVenusPlusButton").queryButton();

        Button receiveCactusMinusButton = lookup("#receiveCactusMinusButton").queryButton();
        Button receiveMarsMinusButton = lookup("#receiveMarsMinusButton").queryButton();
        Button receiveMoonMinusButton = lookup("#receiveMoonMinusButton").queryButton();
        Button receiveNeptunMinusButton = lookup("#receiveNeptunMinusButton").queryButton();
        Button receiveVenusMinusButton = lookup("#receiveVenusMinusButton").queryButton();

        clickOn(giveCactusPlusButton);
        clickOn(giveMarsPlusButton);
        clickOn(giveMoonPlusButton);
        clickOn(giveNeptunPlusButton);
        clickOn(giveVenusPlusButton);
        clickOn(giveCactusMinusButton);
        clickOn(giveMarsMinusButton);
        clickOn(giveMoonMinusButton);
        clickOn(giveNeptunMinusButton);
        clickOn(giveVenusMinusButton);
        clickOn(receiveCactusPlusButton);
        clickOn(receiveMarsPlusButton);
        clickOn(receiveMoonPlusButton);
        clickOn(receiveNeptunPlusButton);
        clickOn(receiveVenusPlusButton);
        clickOn(receiveCactusMinusButton);
        clickOn(receiveMarsMinusButton);
        clickOn(receiveMoonMinusButton);
        clickOn(receiveNeptunMinusButton);
        clickOn(receiveVenusMinusButton);

        Assertions.assertEquals(giveCactusPlusButton.getText(), "+");
        Assertions.assertEquals(giveMarsPlusButton.getText(), "+");
        Assertions.assertEquals(giveMoonPlusButton.getText(), "+");
        Assertions.assertEquals(giveNeptunPlusButton.getText(), "+");
        Assertions.assertEquals(giveVenusPlusButton.getText(), "+");
        Assertions.assertEquals(receiveCactusPlusButton.getText(), "+");
        Assertions.assertEquals(receiveMarsPlusButton.getText(), "+");
        Assertions.assertEquals(receiveMoonPlusButton.getText(), "+");
        Assertions.assertEquals(receiveNeptunPlusButton.getText(), "+");
        Assertions.assertEquals(receiveVenusPlusButton.getText(), "+");

        Assertions.assertEquals(giveCactusMinusButton.getText(), "-");
        Assertions.assertEquals(giveMarsMinusButton.getText(), "-");
        Assertions.assertEquals(giveMoonMinusButton.getText(), "-");
        Assertions.assertEquals(giveNeptunMinusButton.getText(), "-");
        Assertions.assertEquals(giveVenusMinusButton.getText(), "-");
        Assertions.assertEquals(receiveCactusMinusButton.getText(), "-");
        Assertions.assertEquals(receiveMarsMinusButton.getText(), "-");
        Assertions.assertEquals(receiveMoonMinusButton.getText(), "-");
        Assertions.assertEquals(receiveNeptunMinusButton.getText(), "-");
        Assertions.assertEquals(receiveVenusMinusButton.getText(), "-");
    }

    @Test
    public void testOfferBank4to1() {
        when(pioneersService.tradeBank(any(), any())).thenReturn(Observable.just(new Move("0", "m1", "g1", "u1", "build", 1, null, null, resources, "b1", null)));
        Label giveCactusLabel = lookup("#giveCactusLabel").query();
        Label receiveMarsLabel = lookup("#receiveMarsLabel").query();

        Button giveCactusPlusButton = lookup("#giveCactusPlusButton").queryButton();
        clickOn(giveCactusPlusButton);
        clickOn(giveCactusPlusButton);
        clickOn(giveCactusPlusButton);
        clickOn(giveCactusPlusButton);
        Button receiveMarsPlusButton = lookup("#receiveMarsPlusButton").queryButton();
        clickOn(receiveMarsPlusButton);
        Button tradeBank = lookup("#offerBank").queryButton();
        clickOn(tradeBank);
        press(KeyCode.SPACE);


        Assertions.assertEquals(receiveMarsLabel.getText(), "0");
        Assertions.assertEquals(giveCactusLabel.getText(), "0");
    }

    @Test
    public void testOfferPlayer() {
        when(pioneersService.tradePlayer(any(), any(), any(), any())).thenReturn(Observable.just(new Move("0", "m1", "g1", "u1", "build", 1, null, null, resources, "b1", null)));
        Label giveCactusLabel = lookup("#giveCactusLabel").query();
        Label receiveMarsLabel = lookup("#receiveMarsLabel").query();

        Button giveCactusPlusButton = lookup("#giveCactusPlusButton").queryButton();
        clickOn(giveCactusPlusButton);
        clickOn(giveCactusPlusButton);
        clickOn(giveCactusPlusButton);
        Button receiveMarsPlusButton = lookup("#receiveMarsPlusButton").queryButton();
        clickOn(receiveMarsPlusButton);
        Button tradeBank = lookup("#offerPlayer").queryButton();
        clickOn(tradeBank);
        press(KeyCode.SPACE);


        Assertions.assertEquals(receiveMarsLabel.getText(), "0");
        Assertions.assertEquals(giveCactusLabel.getText(), "0");

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        resources.clear();
        tradingSubController = null;
        pioneersService = null;
        eventListener = null;
        gameStorage = null;
        idStorage = null;
    }
}
